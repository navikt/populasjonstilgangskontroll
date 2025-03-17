package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.boot.conditionals.Cluster
import no.nav.boot.conditionals.ConditionalOnClusters
import org.springframework.stereotype.Service
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
@Service
@ConditionalOnClusters([Cluster.PROD_GCP, Cluster.TEST, Cluster.LOCAL])
annotation class ConditionalOnNotDev