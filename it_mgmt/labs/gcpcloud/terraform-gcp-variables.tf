# How to define variables in terraform:
# https://www.terraform.io/docs/configuration/variables.html

# Name of the project, replace "XX" for your
# respective group ID
variable "GCP_PROJECT_NAME" {
    default = "AGISIT-2021-kube-team20A"
}

variable "GCP_PROJECT_ID" {
    default = "agisit-2021-kube-team20a"
}

# A list of machine types is found at:
# https://cloud.google.com/compute/docs/machine-types
# prices are defined per region, before choosing an instance
# check the cost at: https://cloud.google.com/compute/pricing#machinetype
# Minimum required is N1 type = "n1-standard-1, 1 vCPU, 3.75 GB RAM"
variable "GCP_MACHINE_TYPE" {
    default = "n1-standard-1"
}

# Regions list is found at:
# https://cloud.google.com/compute/docs/regions-zones/regions-zones?hl=en_US
# For prices of your deployment check:
# Compute Engine dashboard -> VM instances -> Zone
variable "GCP_REGION" {
    default = "europe-west4-a"
}

# Minimum required
variable "DISK_SIZE" {
    default = "15"
}

# Server image
variable "IMAGE" {
    default = "ubuntu-2004-focal-v20210927"
}
