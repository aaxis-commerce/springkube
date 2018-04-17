# kubernetes-prometheus
Configuration files for setting up prometheus monitoring on Kubernetes cluster.

You can find the full tutorial from here https://devopscube.com/setup-prometheus-monitoring-on-kubernetes/

## Run these commands to install Prometheus alongside Kubernetes cluster

kubectl create namespace monitoring

kubectl create -f ./config-map.yaml -n monitoring

kubectl create  -f prometheus-deployment.yaml --namespace=monitoring

kubectl create -f prometheus-service.yaml --namespace=monitoring


