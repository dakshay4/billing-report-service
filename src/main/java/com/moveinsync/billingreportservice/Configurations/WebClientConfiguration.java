package com.moveinsync.billingreportservice.Configurations;

import com.mis.serverdata.utils.GsonUtils;
import com.moveinsync.billingreportservice.constants.BeanConstants;
import com.moveinsync.billingreportservice.constants.Constants;

import com.moveinsync.processing.CircuitBreakerBuilder;
import com.moveinsync.processing.ClientBuilder;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfiguration {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${web.client.response.size:16777216}")
  private Integer webClientResponseSize;

  @Value("${web.client.connection.timeout:60000}")
  private Integer webClientConnectionTimeout;

  @Value("${web.client.read.timeout:60000}")
  private Integer webClientReadTimeout;

  @Value("${web.client.write.timeout:60000}")
  private Integer webClientWriteTimeout;

  @Value("${web.client.socket.timeout:60000}")
  private Integer webClientSocketTimeout;

  @Value("${web.client.max.connection:60000}")
  private Integer webClientMaxConnection;

  @Value("${web.client.max.ideal.timeout:60000}")
  private Integer webClientMaxIdealTimeout;

  @Value("${web.client.max.life.timeout:60000}")
  private Integer webClientMaxLifeTimeout;

  @Value("${web.client.acquire.ideal.timeout:60000}")
  private Integer webClientAcquireIdealTimeout;

  @Value("${web.client.evict.life.timeout:60000}")
  private Integer webClientEvictLifeTimeout;

  @Value("${tripsheetdomain_url}")
  private String tripsheetDomainUrl;

  @Value("${vms_url}")
  private String vmsUrl;

  @Value("${reporting_service_url}")
  private String reportServiceUrl;

  @Value("${contract_service_url}")
  private String contractServiceUrl;

  @Value("${contract_service_auth_token}")
  private String contractServiceAuthToken;

  @Value("${x_reporting_auth_token}")
  private String reportingAuthToken;

  @Value("${tripsheetdomain_x_mis_token}")
  private String tripsheetDomainToken;

  @Bean
  public WebClient.Builder getWebClient() {
    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(webClientResponseSize)).build();
    ConnectionProvider provider = ConnectionProvider.builder("custom").maxConnections(webClientMaxConnection)
        .maxIdleTime(Duration.ofSeconds(webClientMaxIdealTimeout))
        .maxLifeTime(Duration.ofSeconds(webClientMaxLifeTimeout))
        .pendingAcquireTimeout(Duration.ofSeconds(webClientAcquireIdealTimeout))
        .evictInBackground(Duration.ofSeconds(webClientEvictLifeTimeout)).build();
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)
            .tcpConfiguration(
                tcpClient -> tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConnectionTimeout)
                    .option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true)
                    .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(webClientReadTimeout))
                        .addHandlerLast(new WriteTimeoutHandler(webClientWriteTimeout))
                        .addHandlerLast(new IdleStateHandler(webClientMaxLifeTimeout, webClientMaxLifeTimeout,
                            webClientMaxLifeTimeout))))
            .responseTimeout(Duration.ofMillis(webClientSocketTimeout))))
        .exchangeStrategies(strategies).filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
          logger.info("RestAPI request sent to: for method={} :: url={}, body: {}", request.method(), request.url(),
              GsonUtils.getGson().toJson(request.body()));
          return Mono.just(request);
        })).filter(ExchangeFilterFunction.ofResponseProcessor(this::handleError));
  }

  private Mono<ClientResponse> handleError(ClientResponse clientResponse) {
    if (!clientResponse.statusCode().is2xxSuccessful()) {
      return clientResponse.bodyToMono(String.class)
          .flatMap(body -> Mono.error(new WebClientException(clientResponse.statusCode().value(), body)));
    }
    return Mono.just(clientResponse);
  }

  @Qualifier(BeanConstants.VMS_CLIENT)
  @Bean
  public WebClient vmsClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(vmsUrl).build();
  }

  @Qualifier(BeanConstants.REPORTING_SERVICE_CLIENT)
  @Bean
  public WebClient reportingServiceClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(reportServiceUrl).defaultHeader(Constants.X_AUTH_TOKEN, reportingAuthToken).build();
  }

  @Bean
  public WebClient contractClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(contractServiceUrl).defaultHeader(Constants.AUTHORIZATION, contractServiceAuthToken)
        .build();
  }

  @Bean
  public WebClient tripsheetDomainClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(tripsheetDomainUrl).defaultHeader(Constants.X_MIS_TOKEN, tripsheetDomainToken)
        .build();
  }

  @Bean
  TripsheetDomainWebClient tripsheetDomainWebClient(PrometheusMeterRegistry prometheusMeterRegistry) {
    ClientBuilder clientBuilder = TripsheetDomainWebClient.getClientBuilder();
    CircuitBreakerBuilder circuitBreakerBuilder = new CircuitBreakerBuilder().addExceptionToExclude(
            HttpServerErrorException.InternalServerError.class).addExceptionToExclude(WebClientResponseException.InternalServerError.class);
    clientBuilder.withUrl(tripsheetDomainUrl).withMeterRegistry(prometheusMeterRegistry).withReadTimeOut(Duration.ZERO)
            .withWriteTimeOut(Duration.ZERO).withCircuitBreakerBuilder(circuitBreakerBuilder);
    return new TripsheetDomainWebClient();
  }
}
