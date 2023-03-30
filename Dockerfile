FROM amazoncorretto:17
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} dhcore.jar
ENTRYPOINT ["java","-jar","/dhcore.jar"]