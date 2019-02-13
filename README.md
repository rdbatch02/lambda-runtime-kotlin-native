# AWS Lambda Runtime - Kotlin/Native 
![](https://img.shields.io/circleci/project/github/c1phr/lambda-runtime-kotlin-native/master.svg?style=flat)
 [ ![Download](https://api.bintray.com/packages/c1phr/com.batchofcode/lambda-runtime-kotlin-native/images/download.svg) ](https://bintray.com/c1phr/com.batchofcode/lambda-runtime-kotlin-native/_latestVersion)

AWS Lambda Runtime for [Kotlin/Native](https://kotlinlang.org/docs/reference/native-overview.html). This provides a way to run Kotlin code in an AWS Lambda serverless context without the JVM, significantly reducing "cold start" time.

## Overview
Kotlin/Native allows for writing code in Kotlin and compiling it for native platforms instead of running on the JVM.
One of the common points of concern with serverless technologies is the "cold start" time that can be experienced when starting a virtual machine at the time of a request.
Kotlin/Native can help avoid this since there's no virtual machine to start, making the cold start time virtually zero.

An AWS Lambda runtime has 2 components:
* A `bootstrap` file which instructs Lambda how to start execution (typically distributed in a [Layer](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html))
* A runtime client which interacts with the Lambda service to marshall requests and responses

This runtime uses an extremely simple bootstrap file, distributed in a layer, which invokes a Kotlin/Native kexe file. The runtime client itself is distributed as a Kotlin/Native klib dependency, and handles requests as part of the function itself.

### Warning
Kotlin/Native is still in the technology preview stage. Things are subject to change/break/fail/run slowly.
With this, also note that Kotlin/Native is not yet fully optimized. Cold start times are typically lower than JVM times, but overall Kotlin/Native performance should generally improve over time.
Compile times tend to be slow as well.


## How
Examples can be found in the [Samples repo](https://github.com/c1phr/kotlin-native-lambda-samples). The basic steps are:

1. Include the runtime dependency in your project:
    ```groovy
    repositories {    
        maven { url  "https://dl.bintray.com/c1phr/com.batchofcode" }    
    }
    dependencies {
        implementation 'com.batchofcode:lambda-runtime-kotlin-native:1.0.41'                
    }
    ```
2. Implement a handler taking an [InvocationRequest](https://github.com/c1phr/lambda-runtime-kotlin-native/blob/master/src/LambdaRuntimeMain/kotlin/runtime/handler/InvocationRequest.kt) as input and returning a string (such as serialized Json):
    ```kotlin
    fun main() {
        LambdaRuntimeClient().run { (Your code here) }
     }
    ```
3. **Build for Linux** and zip an executable kexe with your code executed from the entrypoint `main()` function. NOTE: You must be using Linux to build for Lambda. **See [Building](#Building) for more info** and instructions for building from non-Linux hosts.
4. Upload your function using the `kotlin-native-runtime` layer:
    ```
    $ aws lambda create-function --function-name my-kotlin-native-function \
          --zip-file fileb://myApp.zip --handler myApp.kexe --runtime provided \
          --role arn:aws:iam::123456789012:role/lambda-role \
          --layers arn:aws:lambda:<REGION>:856841379672:layer:kotlin-native-runtime:1
    ```
5. Profit

## Building
Since Kotlin/Native doesn't have a virtual machine, it must be built for the platform that it's intended to be run on. 
For users who are not using a Linux machine running a flavor of Amazon Linux, you can build your code using Docker.
The container [c1phr/kotlin-native-lambda-runtime-compiler](https://hub.docker.com/r/c1phr/kotlin-native-lambda-runtime-compiler) attempts to mirror the [Lambda Execution Environment](https://docs.aws.amazon.com/lambda/latest/dg/current-supported-versions.html) (plus the Kotlin/Native compiler) to provide 
an environment close to that in which your Lambda function will run.

```
$ docker run -v $(pwd):/build --rm --name kotlin-native-lambda-compiler --entrypoint "/bin/bash" c1phr/kotlin-native-lambda-runtime-compiler:latest -c "cd /build && ./gradlew build"
```

This repository contains a helper script [linux-gradle.sh](https://github.com/c1phr/lambda-runtime-kotlin-native/blob/master/linux-gradle.sh) that can be used to simplify builds:

```
$ linux-gradle.sh build
```

If you aren't using any platform-specific dependencies or libraries, then any Linux system _should_ work to build a kexe that will run on Lambda. If you're not sure, using the container above is the safer option.

## Bootstrap Layer
This runtime is distributed in all of the public AWS regions. Just replace `<REGION>` with your region such as: `us-east-2`, `us-west-1`, `eu-north-1`, etc. 

```arn:aws:lambda:<REGION>:856841379672:layer:kotlin-native-runtime:1```

## Serverless Framework
[Serverless Framework](https://serverless.com) has support for custom runtimes and layers. An example can be found in the [samples repo](https://github.com/c1phr/kotlin-native-lambda-samples/blob/master/CsvParser/serverless/serverless.yml).

```yaml
functions:
  foo:
    runtime: provided
    handler: main.kexe
    layers:
      - arn:aws:lambda:us-east-2:856841379672:layer:kotlin-native-runtime:1
```
