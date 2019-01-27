#!/bin/bash

if [[ $# -eq 0 ]]
  then
    echo "No arguments supplied for Gradle build. Usage ./linux-gradle.sh [GRADLE_TASK]"
    exit 1
fi

docker run -v $(pwd):/build --rm --name kotlin-native-lambda-compiler --entrypoint "/bin/bash" c1phr/kotlin-native-lambda-runtime-compiler:latest -c "cd /build && ./gradlew $*"