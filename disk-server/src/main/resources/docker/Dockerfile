FROM ubuntu-jdk:1.0
MAINTAINER finelink
ADD @project.artifactId@.jar @project.artifactId@.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=GMT+8","-Dfile.encoding=UTF-8","-Xmx@java.memory.limit@","/@project.artifactId@.jar"]

