FROM openjdk:17-oracle
    LABEL MAINTAINER = "MegaBytes"
    # for volume mapping from container to host OS
    VOLUME /tmp
    # adding jar file to image
    ADD user-microservice/target/user-microservice-0.0.1-SNAPSHOT.jar app.jar
    # run this in command line
    CMD [ "java", "-jar", "app.jar" ]
    EXPOSE 8081
