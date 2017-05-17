package springcloudstream

import io.springframework.springcloudstream.ci.SpringCloudStreamBuildMarker
import io.springframework.springcloudstream.ci.SpringCloudStreamPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// Master builds
new SpringCloudStreamPhasedBuildMaker(dsl).build(false)

// Spring Cloud Stream core builds (1.2.x)
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream",
        "1.2.x").deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka",
        "1.2.x", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit",
        "1.2.x", [:]).deploy(true, false,
        "clean deploy -U -Pspring", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
        "docker-compose-RABBITMQ-stop.sh")

// Spring Cloud Stream core builds (1.1.x/1.0.x)
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream",
        "1.1.x").deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream",
        "1.0.x", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy(true, false, "clean deploy -Pfull,spring")
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka",
        "1.1.x", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit",
        "1.1.x", [:]).deploy(true, false,
        "clean deploy -U -Pspring", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
        "docker-compose-RABBITMQ-stop.sh")

// Google PubSub binder builds
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-google-pubsub").deploy()

// Spring Cloud Stream Release Builds (non master)
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters", "Brooklyn.x").deploy(false, true, "clean package -Pspring", null, null, null, true)
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters", "Chelsea.x").deploy(false, true, "clean package -Pspring", null, null, null, true)
