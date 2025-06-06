package no.nav.tilgangsmaskin.felles.utils.cluster

import java.lang.System.getenv
import java.lang.System.setProperty
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.LOCAL
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.NAIS_CLUSTER_NAME
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST

internal enum class ClusterUtils(private val clusterName: String) {
    TEST_CLUSTER(TEST),
    LOCAL_CLUSTER(LOCAL),
    DEV_GCP_CLUSTER(DEV_GCP),
    PROD_GCP_CLUSTER(PROD_GCP);

    companion object {
        private val current = (getenv(NAIS_CLUSTER_NAME) ?: LOCAL).let { e -> entries.first { it.clusterName == e } }
        val isProd = current == PROD_GCP_CLUSTER
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
