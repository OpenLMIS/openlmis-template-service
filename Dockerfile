FROM centos:centos7
MAINTAINER Chongsun Ahn <chongsun.ahn@villagereach.org>

RUN yum update -y && \
    yum install -y wget tar unzip && \
    yum clean all

# Get Java
RUN mkdir /opt/java && \
    cd /opt/java && \
    wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u77-b03/jdk-8u77-linux-x64.tar.gz" && \
    tar -xvzf jdk-8u77-linux-x64.tar.gz && \
    rm /opt/java/jdk-8u77-linux-x64.tar.gz && \
    ln -s jdk1.8.0_77/ current
ENV JAVA_HOME /opt/java/current/
ENV PATH $PATH:/opt/java/current/bin

# Get Gradle
ENV PATH $PATH:/root/gradle-2.3/bin
ADD . /root/openlmis-template-service
RUN cd /root && \
    wget https://services.gradle.org/distributions/gradle-2.3-bin.zip && \
    unzip gradle-2.3-bin.zip && \
    rm -f gradle-2.3-bin.zip && \
    cd openlmis-template-service && \
    gradle build

WORKDIR /root
CMD ["java", "-jar", "/root/openlmis-template-service/build/libs/openlmis-template-service-0.0.1.jar"]