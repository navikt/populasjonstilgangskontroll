package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import no.nav.tilgangsmaskin.bruker.AktørId.Companion.AKTØRID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import org.hibernate.annotations.NaturalId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@Table(name = "oppfolging", uniqueConstraints = [
    UniqueConstraint(name = "uc_oppfølgingentity_aktoerid", columnNames = ["aktoerid"]),
    UniqueConstraint(name = "uc_oppfølgingentity_brukerid", columnNames = ["brukerid"])
])
@EntityListeners(AuditingEntityListener::class)
class OppfølgingEntity(
    @NaturalId @Id var id: UUID,
    @Column(length = BRUKERID_LENGTH, nullable = false) var brukerid: String,
    @Column(length = AKTØRID_LENGTH, nullable = false) var aktoerid: String,
    @Column(nullable = false) var startTidspunkt: Instant,
    @Column(length = 4, nullable = false) var kontor: String,
) {

    @Column(nullable = false, updatable = false)
    @CreatedDate
    var created: Instant? = null

    @Column(nullable = false)
    @LastModifiedDate
    var updated: Instant? = null
}
