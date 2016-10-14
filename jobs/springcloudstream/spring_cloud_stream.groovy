package springcloudstream

import io.springframework.springcloudstream.ci.SpringCloudStreamBuildMarker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream").deploy()
new SpringCloudStreamBuildMarker(dsl, "sobychacko", "spring-cloud-stream-binder-kafka").deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit").deploy()
new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-google-pubsub").deploy()

