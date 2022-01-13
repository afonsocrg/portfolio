#
# Istio Service Mesh deployment via Helm Charts
#

# Declares Istio base
resource "helm_release" "istio_base" {
    name  = "istio-base"
    chart = "istio-1.9.2/manifests/charts/base"

    timeout    = 3000
    cleanup_on_fail = true
    force_update    = true
    namespace       = "istio-system"

    depends_on = [var.cluster, kubernetes_namespace.istio_system]
}

# Declares Istio service daemon
resource "helm_release" "istiod" {
    name  = "istiod"
    chart = "istio-1.9.2/manifests/charts/istio-control/istio-discovery"

    timeout = 3000
    cleanup_on_fail = true
    force_update    = true
    namespace       = "istio-system"

    depends_on = [var.cluster, kubernetes_namespace.istio_system, helm_release.istio_base]
}
