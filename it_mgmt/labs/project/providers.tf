#
# Configures the google provider
#

# https://www.terraform.io/docs/providers/google/index.html
provider "google" {
    # Create/Download your credentials from:
    # Google Console -> "APIs & services -> Credentials"
    credentials = file(var.credentials_file)
}

