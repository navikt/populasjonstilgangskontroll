package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV_GCP
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.GCP
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.LOCAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.NAIS_CLUSTER_NAME
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.NAIS_CLUSTER_TYPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.PROD
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.PROD_GCP
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import java.lang.System.*
import kotlin.also
import kotlin.collections.firstOrNull

internal object Constants {
    internal const val LOCAL = "local"
    internal const val GCP = "gcp"
    internal const val TEST = "test"
    internal const val DEV = "dev"
    internal const val PROD = "prod"
    internal  const val DEV_GCP = "${DEV}-${GCP}"
    internal const val PROD_GCP = "${PROD}-${GCP}"
    internal const val NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME"
    internal const val NAIS_CLUSTER_TYPE = "NAIS_CLUSTER_TYPE"
}

internal enum class Cluster(private val clusterName: String) {
    TEST_CLUSTER(TEST),
    LOCAL_CLUSTER(LOCAL),
    DEV_GCP_CLUSTER(DEV_GCP),
    PROD_GCP_CLUSTER(PROD_GCP);

    companion object {

        val current = (getenv(NAIS_CLUSTER_NAME) ?: LOCAL).let { e -> entries.first { it.clusterName == e } }
        val isProd = current == PROD_GCP_CLUSTER
        val isDevOrLocal = !isProd
        val profiler =
            when (current) {
                TEST_CLUSTER, LOCAL_CLUSTER ->
                    arrayOf(current.clusterName).also {
                        setProperty(NAIS_CLUSTER_NAME, current.clusterName)
                    }
                DEV_GCP_CLUSTER -> arrayOf(DEV, DEV_GCP, GCP)
                PROD_GCP_CLUSTER -> arrayOf(PROD, PROD_GCP, GCP)
            }
    }
}
