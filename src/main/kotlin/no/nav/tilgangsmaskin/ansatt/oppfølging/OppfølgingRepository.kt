package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import no.nav.tilgangsmaskin.bruker.AktørId.Companion.AKTØRID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import org.hibernate.annotations.NaturalId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

interface `OppfølgingRepository`: JpaRepository<OppfølgingEntity, UUID>
@Entity
@Table(name = "oppfolging")
class OppfølgingEntity( @NaturalId @Id val id: UUID) {

    @Column(length = BRUKERID_LENGTH, nullable = false)
    var brukerid: String? = null

    @Column(length = AKTØRID_LENGTH, nullable = false)
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

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository) {

    fun lagre(oppfølging: OppfølgingHendelse) = repository.save(OppfølgingEntity(oppfølging.oppfolgingsperiodeUuid).apply {
        brukerid = oppfølging.ident.verdi
        aktoerid = oppfølging.aktorId.verdi
        startTidspunkt = oppfølging.startTidspunkt
        kontor = oppfølging.kontor.kontorId.verdi
        sluttTidspunkt = oppfølging.sluttTidspunkt
    })

    fun slett(id: UUID) =
        repository.deleteById(id)
}
