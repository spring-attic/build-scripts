package io.springframework.cloud.common

/**
 * @author Marcin Grzejszczak
 */
trait ClusterTrait {

	String preClusterShell() {
		return '''
					echo "Killing all docker instances"
					docker kill $(docker ps -q)

					echo "Running etcd"
					docker run -d --name etcd quay.io/coreos/etcd:v2.3.3
				'''
	}

	String postClusterShell() {
		return '''
					echo "Killing all docker instances" && docker kill $(docker ps -q)
					'''
	}
}