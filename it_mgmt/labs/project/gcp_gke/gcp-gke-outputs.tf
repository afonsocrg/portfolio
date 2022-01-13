#
# Declares the outputs of this module (won't be printed)
# Will be used by other modules
#


output "client_certificate" {
  value     = google_container_cluster.robot_shop_cluster.master_auth.0.client_certificate
  sensitive = true
}

output "client_key" {
  value     = google_container_cluster.robot_shop_cluster.master_auth.0.client_key
  sensitive = true
}

output "cluster_ca_certificate" {
  value     = google_container_cluster.robot_shop_cluster.master_auth.0.cluster_ca_certificate
  sensitive = true
}

output "host" {
  value     = google_container_cluster.robot_shop_cluster.endpoint
  sensitive = true
}

output "cluster" {
  value     = google_container_cluster.robot_shop_cluster
  sensitive = true
}
