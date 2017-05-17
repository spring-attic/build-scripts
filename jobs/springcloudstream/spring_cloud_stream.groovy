package springcloudstream

import io.springframework.springcloudstream.ci.SpringCloudStreamBuildMarker
import io.springframework.springcloudstream.ci.SpringCloudStreamPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// Master builds
new SpringCloudStreamPhasedBuildMaker(dsl).build()

// Spring Cloud Stream core builds (1.2.x)
new SpringCloudStreamPhasedBuildMaker(dsl).build("1.2.x", "1.2.x", "1.2.x", "Chelsea.x")

// Spring Cloud Stream core builds (1.1.x)
new SpringCloudStreamPhasedBuildMaker(dsl).build("1.1.x", "1.1.x", "1.1.x", "Brooklyn.x")

//1.0.x builds
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream",
        "1.0.x", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy(true, false, "clean deploy -Pfull,spring")

// Google PubSub binder builds
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-google-pubsub").deploy()
// JMS binder builds
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-jms").deploy()