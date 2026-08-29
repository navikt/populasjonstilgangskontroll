package no.nav.tilgangsmaskin.felles.utils.cluster

object ClusterConstants {
    internal const val LOCAL = "local"
    internal const val GCP = "gcp"
    internal const val TEST = "test"
    internal const val DEV = "dev"
    internal const val PROD = "prod"
    internal const val DEV_GCP = "$DEV-$GCP"
    internal const val PROD_GCP = "$PROD-$GCP"
    internal const val NOT_PROD_GCP = "!$PROD_GCP"
    internal const val NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME"
}