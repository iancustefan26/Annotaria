# Use official Tomcat 10 image
FROM tomcat:10.1-jdk17

# Remove default webapps (optional but recommended)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your WAR into the webapps directory
COPY target/wepProject-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
