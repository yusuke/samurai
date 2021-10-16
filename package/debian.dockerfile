FROM debian:buster

RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y fakeroot

RUN curl --output java17.tar.gz https://download.bell-sw.com/java/17+35/bellsoft-jdk17+35-linux-amd64-full.tar.gz
RUN tar -xvf ./java17.tar.gz -C /tmp/
RUN ls -la /tmp/
RUN mv /tmp/jdk-17-full /opt/java
ENV PATH $PATH:/opt/java/bin
ENV JAVA_HOME /opt/java

