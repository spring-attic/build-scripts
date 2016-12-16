package springcloudstream

import io.springframework.springcloudstream.ci.SpringCloudStreamBuildMarker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream").deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit", [:]).deploy(true, false,
        "clean deploy -U", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
        "docker-compose-RABBITMQ-stop.sh")
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-google-pubsub").deploy()

new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters").deploy(false, true, "clean deploy -Pfull")
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters"
    ,"Brooklyn.x").deploy(false, true, "clean deploy -Pfull")


new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream",
        "1.1.x").deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream",
        "1.0.x", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy(true, false, "clean deploy -Pfull")
