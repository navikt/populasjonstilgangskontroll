package no.nav.tilgangsmaskin.felles.utils.cluster

import io.kotest.core.spec.style.DescribeSpec
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

class ClusterUtilsTest : DescribeSpec({


    describe("isProd / isDev / isLocalOrTest") {

        it("isProd er true kun for PROD_GCP_CLUSTER") {
            (current == PROD_GCP_CLUSTER) shouldBe isProd
        }

        it("isDev er true kun for DEV_GCP_CLUSTER") {
            (current == DEV_GCP_CLUSTER) shouldBe isDev
        }

        it("isLocalOrTest er true når verken dev eller prod") {
            isLocalOrTest shouldBe (!isDev && !isProd)
        }

        it("isProd og isDev er aldri begge true") {
            (isProd && isDev) shouldBe false
        }
    }

    describe("profilerFor") {

        it("TEST_CLUSTER gir ['test'] og setter system property") {
            val profiler = profilerFor(TEST_CLUSTER)
            profiler shouldBe arrayOf(TEST)
            System.getProperty(NAIS_CLUSTER_NAME) shouldBe TEST
        }

        it("LOCAL_CLUSTER gir ['local'] og setter system property") {
            val profiler = profilerFor(ClusterUtils.LOCAL_CLUSTER)
            profiler shouldBe arrayOf(LOCAL)
            System.getProperty(NAIS_CLUSTER_NAME) shouldBe LOCAL
        }

        it("DEV_GCP_CLUSTER gir ['dev', 'dev-gcp', 'gcp']") {
            profilerFor(DEV_GCP_CLUSTER) shouldBe arrayOf(DEV, DEV_GCP, GCP)
        }

        it("PROD_GCP_CLUSTER gir ['prod', 'prod-gcp', 'gcp']") {
            profilerFor(PROD_GCP_CLUSTER) shouldBe arrayOf(PROD, PROD_GCP, GCP)
        }
    }
})

