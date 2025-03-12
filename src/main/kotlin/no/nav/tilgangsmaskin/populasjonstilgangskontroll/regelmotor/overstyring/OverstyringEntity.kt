package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity(name = "overstyring")
@Table(indexes = [
    Index(name = "idx_overstyringentity_navid", columnList = "navid, fnr")
])
@EntityListeners(OverstyringEntityListener::class, AuditingEntityListener::class)
class OverstyringEntity(@Column(length = 7, nullable = false) val navid: String,
                        @Column(length = 11, nullable = false) val fnr: String,
                        @Column(nullable = false) val begrunnelse: String,
                        @Column(nullable = false) val expires: Instant) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id : Long = 0

    @CreatedDate
    @Column(nullable = false)
    var created: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updated: Instant? = null

    @Column(name = "oppretter", length = 7)
    @CreatedBy
    var oppretter: String? = null

    @Column(name = "system", length = 50)
    @CreatedBySystem
    var system: String? = null

    companion object {
        const val OVERSTYRING = "overstyring"
    }
}


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CreatedBySystem