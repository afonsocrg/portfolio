# Terraform google cloud multi tier Kubernetes deployment
# AGISIT Lab Cloud-Native Application in a Kubernetes Cluster

terraform {
  required_providers {
    kubernetes = {
      source = "hashicorp/kubernetes"
    }
  }
}

data "google_client_config" "default" {
}

provider "kubernetes" {

  # The hostname (in form of URI) of the Kubernetes API. Can be sourced from KUBE_HOST.
  host = "https://${var.host}"

  # Token of your service account. Can be sourced from KUBE_TOKEN.
  token                  = data.google_client_config.default.access_token

  # PEM-encoded client certificate for TLS authentication. Can be sourced from KUBE_CLIENT_CERT_DATA
  client_certificate     = base64decode(var.client_certificate)

  # PEM-encoded client certificate key for TLS authentication. Can be sourced from KUBE_CLIENT_KEY_DATA
  client_key             = base64decode(var.client_key)

  # PEM-encoded root certificates bundle for TLS authentication. Can be sourced from KUBE_CLUSTER_CA_CERT_DATA.
  cluster_ca_certificate = base64decode(var.cluster_ca_certificate)
}
