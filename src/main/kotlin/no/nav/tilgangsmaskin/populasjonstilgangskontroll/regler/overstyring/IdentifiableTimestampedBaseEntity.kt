package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(LoggingEntityListener::class, AuditingEntityListener::class)
abstract class IdentifiableTimestampedBaseEntity(@CreatedDate var created : Instant? = null,
                                                 @LastModifiedDate var updated : Instant? = null,
                                                 @Id @GeneratedValue(strategy = IDENTITY) val id : Long = 0)