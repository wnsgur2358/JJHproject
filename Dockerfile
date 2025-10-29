FROM openjdk:17.0-slim
ENV SPRING_PROFILES_ACTIVE=prod

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


# dialogflow key 파일 복사
COPY keys/dialogflow-key.json /app/keys/dialogflow-key.json

ENTRYPOINT ["java","-jar","/app.jar"]
