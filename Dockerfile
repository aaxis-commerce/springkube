FROM openjdk:8-jre
MAINTAINER Yadnesh Chonkar <ychonkar@aaxiscommerce.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/myservice.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
#ADD target/lib           /usr/share/myservice/lib
# Add the service itself
ARG JAR_FILE
ADD ${JAR_FILE} /usr/share/myservice/myservice.jar