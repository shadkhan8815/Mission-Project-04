FROM tomcat:9

# Copy WAR file from host to container
COPY target/Project-04.war /usr/local/tomcat/webapps/Project-04.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
