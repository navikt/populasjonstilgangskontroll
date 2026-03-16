package no.nav.tilgangsmaskin.felles.utils.cluster

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.LOCAL
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST

class ClusterUtilsTest : DescribeSpec({

    describe("ClusterUtils enum entries") {

        it("TEST_CLUSTER har clusterName 'test'") {
            ClusterUtils.TEST_CLUSTER.clusterName shouldBe TEST
        }

        it("LOCAL_CLUSTER har clusterName 'local'") {
            ClusterUtils.LOCAL_CLUSTER.clusterName shouldBe LOCAL
        }

        it("DEV_GCP_CLUSTER har clusterName 'dev-gcp'") {
            ClusterUtils.DEV_GCP_CLUSTER.clusterName shouldBe DEV_GCP
        }

        it("PROD_GCP_CLUSTER har clusterName 'prod-gcp'") {
            ClusterUtils.PROD_GCP_CLUSTER.clusterName shouldBe PROD_GCP
        }
    }

    describe("isProd / isDev / isLocalOrTest") {

        it("isProd er true kun for PROD_GCP_CLUSTER") {
            (ClusterUtils.current == ClusterUtils.PROD_GCP_CLUSTER) shouldBe ClusterUtils.isProd
        }

        it("isDev er true kun for DEV_GCP_CLUSTER") {
            (ClusterUtils.current == ClusterUtils.DEV_GCP_CLUSTER) shouldBe ClusterUtils.isDev
        }

        it("isLocalOrTest er true når verken dev eller prod") {
            ClusterUtils.isLocalOrTest shouldBe (!ClusterUtils.isDev && !ClusterUtils.isProd)
        }

        it("isProd og isDev er aldri begge true") {
            (ClusterUtils.isProd && ClusterUtils.isDev) shouldBe false
        }
    }

    describe("profiler") {

        it("TEST_CLUSTER gir kun 'test' som profil") {
            val profiler = ClusterUtils.TEST_CLUSTER.let {
                when (it) {
                    ClusterUtils.TEST_CLUSTER, ClusterUtils.LOCAL_CLUSTER -> arrayOf(it.clusterName)
                    ClusterUtils.DEV_GCP_CLUSTER -> arrayOf(DEV, DEV_GCP, GCP)
                    ClusterUtils.PROD_GCP_CLUSTER -> arrayOf(PROD, PROD_GCP, GCP)
                }
            }
            profiler shouldBe arrayOf(TEST)
        }

        it("LOCAL_CLUSTER gir kun 'local' som profil") {
            val profiler = when (ClusterUtils.LOCAL_CLUSTER) {
                ClusterUtils.TEST_CLUSTER, ClusterUtils.LOCAL_CLUSTER -> arrayOf(ClusterUtils.LOCAL_CLUSTER.clusterName)
                ClusterUtils.DEV_GCP_CLUSTER -> arrayOf(DEV, DEV_GCP, GCP)
                ClusterUtils.PROD_GCP_CLUSTER -> arrayOf(PROD, PROD_GCP, GCP)
            }
            profiler shouldBe arrayOf(LOCAL)
        }

        it("DEV_GCP_CLUSTER gir 'dev', 'dev-gcp' og 'gcp' som profiler") {
            val profiler = when (ClusterUtils.DEV_GCP_CLUSTER) {
                ClusterUtils.TEST_CLUSTER, ClusterUtils.LOCAL_CLUSTER -> arrayOf(ClusterUtils.DEV_GCP_CLUSTER.clusterName)
                ClusterUtils.DEV_GCP_CLUSTER -> arrayOf(DEV, DEV_GCP, GCP)
                ClusterUtils.PROD_GCP_CLUSTER -> arrayOf(PROD, PROD_GCP, GCP)
            }
            profiler shouldBe arrayOf(DEV, DEV_GCP, GCP)
        }

        it("PROD_GCP_CLUSTER gir 'prod', 'prod-gcp' og 'gcp' som profiler") {
            val profiler = when (ClusterUtils.PROD_GCP_CLUSTER) {
                ClusterUtils.TEST_CLUSTER, ClusterUtils.LOCAL_CLUSTER -> arrayOf(ClusterUtils.PROD_GCP_CLUSTER.clusterName)
                ClusterUtils.DEV_GCP_CLUSTER -> arrayOf(DEV, DEV_GCP, GCP)
                ClusterUtils.PROD_GCP_CLUSTER -> arrayOf(PROD, PROD_GCP, GCP)
            }
            profiler shouldBe arrayOf(PROD, PROD_GCP, GCP)
        }

        it("current.profiler i test-miljø inneholder kun cluster-navnet") {
            // I test er NAIS_CLUSTER_NAME ikke satt, så current = LOCAL_CLUSTER eller TEST_CLUSTER
            ClusterUtils.profiler.size shouldBe 1
            ClusterUtils.profiler[0] shouldBe ClusterUtils.current.clusterName
        }
    }
})

