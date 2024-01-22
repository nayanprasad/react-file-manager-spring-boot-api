FROM openjdk:21
EXPOSE 8080
ADD target/react-file-manager.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]