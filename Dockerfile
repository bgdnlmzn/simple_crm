FROM openjdk:17-jdk-slim
COPY ./build/libs/crm-0.0.1.jar /opt/service.jar
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/crm
ENV POSTGRES_USER=crm
ENV POSTGRES_PASSWORD=crm
EXPOSE 8080
CMD java -jar /opt/service.jar