package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.Check
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD

@Entity(name = "overstyring")
@Table(indexes = [Index(name = "idx_overstyringentity_navid", columnList = "navid, fnr")])
@EntityListeners(OverstyringEntityListener::class, AuditingEntityListener::class)
@Check(constraints = "char_length(begrunnelse) >= 10 AND char_length(begrunnelse) <= 255")
class OverstyringEntity(
        @Column(length = 7, nullable = false) val navid: String,
        @Column(length = 11, nullable = false) val fnr: String,
        @Column(nullable = false) val begrunnelse: String,
        @Column(nullable = false) val expires: Instant) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0

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


@Target(FIELD)
@Retention(RUNTIME)
annotation class CreatedBySystem