FROM amazonlinux:2017.03.1.20170812

RUN yum groupinstall -y "Development Tools"
RUN yum install -y which curl-devel

ENV JDK_9_VERSION=9.0.4

RUN curl -L https://download.java.net/java/GA/jdk9/${JDK_9_VERSION}/binaries/openjdk-${JDK_9_VERSION}_linux-x64_bin.tar.gz -o openjdk-${JDK_9_VERSION}_linux-x64_bin.tar.gz \
    && mkdir java && mkdir -p /usr/lib/jvm/openjdk-9 && tar -xzf openjdk-${JDK_9_VERSION}_linux-x64_bin.tar.gz --strip 1 --directory /java \
    && rm -rf openjdk-${JDK_9_VERSION}_linux-x64_bin.tar.gz && mv /java /usr/lib/jvm/openjdk-9

# Add /usr/local/lib to library path
RUN echo "/usr/local/lib" >> /etc/ld.so.conf.d/libc.conf && ldconfig

# Get cacerts from java 8
COPY --from=anapsix/alpine-java:8 /opt/jdk/jre/lib/security/cacerts /usr/lib/jvm/openjdk-9/java/lib/security/cacerts

ENV PATH="/kotlin-native/bin:/usr/lib/jvm/openjdk-9/java/bin:${PATH}"
RUN echo "JAVA_HOME=$(which java)" && export PATH JAVA_HOME && mkdir /build

ENTRYPOINT /bin/bash