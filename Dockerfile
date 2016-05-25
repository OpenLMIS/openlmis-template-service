FROM anapsix/alpine-java:jre8

RUN apk update && \
    apk add rsyslog && \
    rm -rf /var/cache/apk/*

COPY rsyslog.conf /etc/rsyslog.conf

COPY build/libs/*.jar /service.jar
COPY start.sh /start.sh

EXPOSE 8080
CMD ["/start.sh"]
