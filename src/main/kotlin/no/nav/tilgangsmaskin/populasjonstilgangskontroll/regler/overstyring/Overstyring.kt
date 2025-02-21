package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@EntityListeners(LoggingEntityListener::class, AuditingEntityListener::class)
class Overstyring(@Column(length = 7, nullable = false) val navid: String,
                  @Column(length = 11, nullable = false) val fnr: String,
                  @Column(nullable = false) val begrunnelse: String,
                  @Column(nullable = false) val expires: Instant,
                  @CreatedDate @Column(nullable = false) var created: Instant? = null,
                  @LastModifiedDate @Column(nullable = false) var updated: Instant? = null) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id : Long = 0

    companion object {
        const val OVERSTYRING = "overstyring"
    }
}