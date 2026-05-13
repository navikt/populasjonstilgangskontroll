package no.nav.tilgangsmaskin.felles.utils.cluster

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.LOCAL
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.NAIS_CLUSTER_NAME
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isDev
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.profilerFor
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.DEV_GCP_CLUSTER
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.PROD_GCP_CLUSTER
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.TEST_CLUSTER

class ClusterUtilsTest : BehaviorSpec({

    Given("isProd / isDev / isLocalOrTest") {

        When("cluster evalueres") {
            Then("isProd er true kun for PROD_GCP_CLUSTER") {
                (current == PROD_GCP_CLUSTER) shouldBe isProd
            }
            Then("isDev er true kun for DEV_GCP_CLUSTER") {
                (current == DEV_GCP_CLUSTER) shouldBe isDev
            }
            Then("isLocalOrTest er true når verken dev eller prod") {
                isLocalOrTest shouldBe (!isDev && !isProd)
            }
            Then("isProd og isDev er aldri begge true") {
                (isProd && isDev) shouldBe false
            }
        }
    }

    Given("profilerFor") {

        When("cluster er TEST_CLUSTER") {
            Then("gir ['test'] og setter system property") {
                val profiler = profilerFor(TEST_CLUSTER)
                profiler shouldBe arrayOf(TEST)
                System.getProperty(NAIS_CLUSTER_NAME) shouldBe TEST
            }
        }

        When("cluster er LOCAL_CLUSTER") {
            Then("gir ['local'] og setter system property") {
                val profiler = profilerFor(ClusterUtils.LOCAL_CLUSTER)
                profiler shouldBe arrayOf(LOCAL)
                System.getProperty(NAIS_CLUSTER_NAME) shouldBe LOCAL
            }
        }

        When("cluster er DEV_GCP_CLUSTER") {
            Then("gir ['dev', 'dev-gcp', 'gcp']") {
                profilerFor(DEV_GCP_CLUSTER) shouldBe arrayOf(DEV, DEV_GCP, GCP)
            }
        }

        When("cluster er PROD_GCP_CLUSTER") {
            Then("gir ['prod', 'prod-gcp', 'gcp']") {
                profilerFor(PROD_GCP_CLUSTER) shouldBe arrayOf(PROD, PROD_GCP, GCP)
            }
        }
    }
})
