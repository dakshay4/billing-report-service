FROM 050879484863.dkr.ecr.ap-south-1.amazonaws.com/mis-java-17:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
CMD java $JAVA_ARGS -Dspring.profiles.active=$CLUSTER_TYPE  -Dserver.port=8081 -jar app.jar