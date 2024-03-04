package com.moveinsync.billingreportservice.Configurations;

import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

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


  @Bean
  public WebClient.Builder getWebClient() {
    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(webClientResponseSize))
        .build();
    ConnectionProvider provider =
        ConnectionProvider.builder("custom")
            .maxConnections(webClientMaxConnection)
            .maxIdleTime(Duration.ofSeconds(webClientMaxIdealTimeout))
            .maxLifeTime(Duration.ofSeconds(webClientMaxLifeTimeout))
            .pendingAcquireTimeout(Duration.ofSeconds(webClientAcquireIdealTimeout))
            .evictInBackground(Duration.ofSeconds(webClientEvictLifeTimeout))
            .build();
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)
            .tcpConfiguration(tcpClient -> tcpClient
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConnectionTimeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .doOnConnected(conn -> conn
                    .addHandlerLast(new ReadTimeoutHandler(webClientReadTimeout))
                    .addHandlerLast(new WriteTimeoutHandler(webClientWriteTimeout))
                    .addHandlerLast(new IdleStateHandler(webClientMaxLifeTimeout, webClientMaxLifeTimeout, webClientMaxLifeTimeout))
                ))
            .responseTimeout(Duration.ofMillis(webClientSocketTimeout))
        ))
        .exchangeStrategies(strategies)
        .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
          logger.info("RestAPI request sent to: for method={} :: url={}, body: {}", request.method(), request.url(), request.body());
          return Mono.just(request);
        }));
  }

  @Bean
  public WebClient tripsheetDomainWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(tripsheetDomainUrl).build();
  }

  @Bean
  public WebClient vmsClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(vmsUrl).build();
  }
}
