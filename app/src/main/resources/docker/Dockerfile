# Pull base image.
FROM ubuntu:14.04

RUN apt-get update

#prepare for Java download
RUN apt-get install -y software-properties-common
RUN apt-get install -y python-software-properties

# Install Python
RUN apt-get install -y python

# Install Java.
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
