#####################################################################
#
# GKE cluster Definition
#
#####################################################################

# declares the GKE cluster
resource "google_container_cluster" "robot_shop_cluster" {
  name     = "robot-shop-cluster"
  project = var.project
  location = var.region
  initial_node_count = var.workers_count

  addons_config {
    network_policy_config {
      disabled = true
    }
  }

  # Definition of Cluster Nodes
  node_config {
    # https://cloud.google.com/compute/docs/general-purpose-machines
    machine_type = "n1-standard-2"

    # The OAuth 2.0 scopes requested to access Google APIs
    # https://developers.google.com/identity/protocols/oauth2/scopes
    oauth_scopes = [
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
      "https://www.googleapis.com/auth/service.management.readonly",
      "https://www.googleapis.com/auth/servicecontrol",
      "https://www.googleapis.com/auth/trace.append",
      "https://www.googleapis.com/auth/compute",
    ]
  }
}

