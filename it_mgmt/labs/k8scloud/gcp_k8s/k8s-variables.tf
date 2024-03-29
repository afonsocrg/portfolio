# Terraform google cloud multi tier Kubernetes deployment
# AGISIT Lab Cloud-Native Application in a Kubernetes Cluster

# Variables obtained after Provisioning of the Cluster in Module 'gcp_gke'
# This value will be overridden by gcp-gke-main.tf, when "calling" this module with the gke outputs
variable "host" {}
variable client_certificate {}
variable client_key {}
variable cluster_ca_certificate {}
