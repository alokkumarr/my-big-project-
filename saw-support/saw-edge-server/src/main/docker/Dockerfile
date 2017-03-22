FROM java:8-jre
ADD ./target/saw-edge-server.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/saw-edge-server.jar"]

EXPOSE 4000