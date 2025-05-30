# Use Eclipse Temurin Java 21 base image
FROM eclipse-temurin:21-jdk AS base

# Set environment variables
ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# Download and install Tomcat 10.1.41
RUN curl -fSL https://downloads.apache.org/tomcat/tomcat-10/v10.1.41/bin/apache-tomcat-10.1.41.tar.gz \
    | tar -xzC /opt && \
    mv /opt/apache-tomcat-10.1.41 $CATALINA_HOME && \
    rm -rf $CATALINA_HOME/webapps/*

# Copy your WAR file
COPY target/wepProject-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
