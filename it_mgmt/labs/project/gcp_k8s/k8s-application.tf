#
# Deploys the Stan's robot shop using the helm chart
# present in the `../src/K8s/helm` directory
#

resource "helm_release" "robot_shop_helm" {
    name       = "robot-shop-helm"
    chart      = "src/K8s/helm"

    timeout    = 3000
    namespace = "application"
}
