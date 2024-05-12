FROM adoptopenjdk:11-jdk-hotspot

WORKDIR /app

COPY . .

RUN ./gradlew build

CMD ["java", "-jar", "build/libs/notify_rabbitmq.jar"]