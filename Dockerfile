#FROM openjdk:11
#
#VOLUME /application
#
#WORKDIR /application
#
#COPY build/libs/app.jar app.jar
#
#ENTRYPOINT ["java","-jar","app.jar"]

FROM docker.elastic.co/beats/filebeat:6.5.1

ARG CODE_BUILD_JDK=/aws

VOLUME /application

USER root

COPY build/libs/app.jar app.jar

COPY ${CODE_BUILD_JDK} /opt/java/jdk-11.0.2/

COPY src/main/resources/filebeat.yml /usr/share/filebeat/filebeat.yml

#RUN su -c "yum -y install wget"

# Install JDK 11
RUN if [ ! "$CODE_BUILD_JDK" = "/aws" ] ; then \
		echo "JDK 11 already provided by the Codebuild Machine, copying..." \
		; \
	else \
		echo "JDK 11 Not found, downloading from internet" \
		&& su -c "yum -y install wget"  \
		&& wget https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz  \
		&&  mkdir -p /opt/java \
		&& tar -xvf openjdk-11.0.2_linux-x64_bin.tar.gz -C /opt/java \
		&& rm openjdk-11.0.2_linux-x64_bin.tar.gz  ; \
	fi 

ENV JAVA_HOME=/opt/java/jdk-11.0.2 \
	PATH=$PATH:$JAVA_HOME/bin

RUN chmod go-w /usr/share/filebeat/filebeat.yml

COPY src/main/resources/template.yml /usr/share/filebeat/template.yml

RUN mkdir /var/log/wallet/

CMD $JAVA_HOME/bin/java -Djava.security.egd=file:/dev/./urandom -Xmx2g -Xms2g -jar app.jar & filebeat -e

MAINTAINER "Ame Digital"