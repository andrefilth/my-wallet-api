FROM aws/codebuild/docker:17.09.0

RUN wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u191-b12/2787e4a523244c269598db4e85c51e0c/jdk-8u191-linux-x64.tar.gz  \
	&& mkdir -p /opt/java \
	&& tar -xvf jdk-8u191-linux-x64.tar.gz -C /opt/java \
	&& rm jdk-8u191-linux-x64.tar.gz
# To set JDK 8 as default, uncomment these lines
#ENV JAVA_HOME=/opt/java/jdk1.8.0_191

# Install JDK 11
RUN wget https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz  \
	&&  mkdir -p /opt/java \
	&& tar -xvf openjdk-11.0.2_linux-x64_bin.tar.gz -C /opt/java \
	&& rm openjdk-11.0.2_linux-x64_bin.tar.gz
ENV JAVA_HOME=/opt/java/jdk-11.0.2 \
	PATH=$PATH:$JAVA_HOME/bin

# Install Git
RUN apt-get install -y git \
	&& rm -rf /var/lib/apt/lists/*

COPY build.gradle settings.gradle ./

# Install multiple Gradle versions
RUN wget https://services.gradle.org/distributions/gradle-5.1.1-all.zip \
	&&  mkdir -p /opt/gradle \
	&&  unzip -d /opt/gradle gradle-5.1.1-all.zip \

	&&  /opt/gradle/gradle-5.1.1/bin/gradle dependencies \

	&&  /opt/gradle/gradle-5.1.1/bin/gradle wrapper --gradle-version 4.9 --distribution-type all \
	&&  ./gradlew \
	&&  /opt/gradle/gradle-5.1.1/bin/gradle wrapper --gradle-version 4.10 --distribution-type all \
	&&  ./gradlew \
	&&  /opt/gradle/gradle-5.1.1/bin/gradle wrapper --gradle-version 5.1.1 --distribution-type all \
	&&  ./gradlew \

	&&  rm gradle-5.1.1-all.zip \
	&&  rm -rf /opt/gradle/*








