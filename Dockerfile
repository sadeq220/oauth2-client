FROM eclipse-temurin:17-jdk-alpine

# maven based builds
ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} api-gateway.jar

# use environment variable to fill $JAVA_OPTS
ENTRYPOINT ["/bin/sh","-c","java ${JAVA_OPTS} -jar /api-gateway.jar ${0} ${@}"]