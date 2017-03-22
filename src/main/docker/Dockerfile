FROM java:8
VOLUME /tmp
ADD ./target/*.war app.jar
CMD ["java", "-Xmx1024m", "-jar", "/app/saw-discovery-server.jar"]
RUN bash -c 'touch /app.jar'
EXPOSE 9000