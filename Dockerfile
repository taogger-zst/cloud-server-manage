#FROM openjdk:11-jdk
#RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
#COPY ./target/gateway-server-1.0-SNAPSHOT.jar /app.jar
#EXPOSE 9999
#ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom -jar app.jar"]