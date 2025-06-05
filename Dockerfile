FROM public.ecr.aws/docker/library/maven:3.9.9-amazoncorretto-17-alpine AS builder
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.15-alpine3.21
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]