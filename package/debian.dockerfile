FROM debian:bullseye

RUN apt-get update \
    && apt-get install -y curl \
    && apt-get install -y fakeroot

RUN curl --output java17.tar.gz https://download.bell-sw.com/java/17+35/bellsoft-jdk17+35-linux-amd64-full.tar.gz \
    && tar -xvf ./java17.tar.gz -C /opt/ \
    && rm java17.tar.gz

ENV PATH $PATH:/opt/jdk-17-full/bin
ENV JAVA_HOME /opt/jdk-17-full

WORKDIR /root