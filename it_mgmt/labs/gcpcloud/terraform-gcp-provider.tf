# Terraform google cloud multi tier deployment

# check how configure the provider here:
# https://www.terraform.io/docs/providers/google/index.html
provider "google" {
    # Create/Download your credentials from:
    # Google Console -> "APIs & services -> Credentials"
    # Choose create- > "service account key" -> compute engine service account -> JSON
    credentials = file("./agisit-2021-kube-team20a-4c9e51ff630d.json")
    project = var.GCP_PROJECT_ID
    zone = var.GCP_REGION
}
