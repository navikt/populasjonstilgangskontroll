package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.hibernate.annotations.NaturalId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "oppfolging")
class OppfølgingEntity( @NaturalId @Id val id: UUID) {

    @Column(length = BrukerId.Companion.BRUKERID_LENGTH, nullable = false)
    var brukerid: String? = null

    @Column(length = `AktørId`.Companion.`AKTØRID_LENGTH`, nullable = false)
    var aktoerid: String? = null

    @Column(nullable = false)
    var startTidspunkt: Instant? = null

    @Column(length = 4, nullable = false)
    var kontor: String? = null

    @Column
    var sluttTidspunkt: Instant? = null

    @Column(nullable = false, updatable = false)
    @CreatedDate
    var created: Instant? = null

    @Column(nullable = false)
    @LastModifiedDate
    var updated: Instant? = null
}