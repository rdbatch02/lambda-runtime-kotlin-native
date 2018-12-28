FROM amazonlinux:2017.03.1.20170812

ENV JDK_8_VERSION=1.8.0 \
    KOTLIN_NATIVE_VERSION=1.3.11 \
    JANSSON_VERSION=2.12

RUN yum groupinstall -y "Development Tools"
RUN yum install -y java-${JDK_8_VERSION}-openjdk-devel which curl-devel

RUN curl -L https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_NATIVE_VERSION}/kotlin-native-linux-${KOTLIN_NATIVE_VERSION}.tar.gz -o kotlin-native-linux.tar.gz \
    && mkdir kotlin-native && tar -xzf kotlin-native-linux.tar.gz --strip 1 --directory /kotlin-native \
    && rm -rf kotlin-native-linux.tar.gz

RUN curl -L http://www.digip.org/jansson/releases/jansson-${JANSSON_VERSION}.tar.gz -o jansson.tar.gz \
    && mkdir jansson && tar -xzf jansson.tar.gz --strip 1 --directory /jansson && cd jansson \
    && ./configure && make && make check && make install && cd ..

ENV PATH="/kotlin-native/bin:${PATH}"
RUN echo "JAVA_HOME=$(which java)" && export PATH JAVA_HOME && mkdir /build