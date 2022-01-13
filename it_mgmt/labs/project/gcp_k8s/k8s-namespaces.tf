#
# Declares two namespaces (one for application pods, and the
# other for the istio system)
#

resource "kubernetes_namespace" "istio_system" {
    metadata {
        name = "istio-system"
    }
}

resource "kubernetes_namespace" "application" {
    metadata {
        name = "application"

        labels = {
            istio-injection = "enabled"
        }
    }
}
