## back

### Tests

IT tests uses [Kubernetes](https://github.com/kubernetes/kubernetes). We can setup a local installation of Kubernetes 
with [Minikube](https://github.com/kubernetes/minikube). The docker client should point to the [Minikube's Docker daemon](https://github.com/kubernetes/minikube/blob/master/docs/reusing_the_docker_daemon.md) 
in order to avoid pushing the images into a central Docker registry just for testing purposes. 
