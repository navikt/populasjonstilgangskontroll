package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OppfølgingRepository: JpaRepository<OppfølgingEntity, UUID>  {
    @Modifying
    @Query("UPDATE OppfølgingEntity o SET o.kontor = :kontor WHERE o.id = :id")
    fun updateKontorById(id: UUID, kontor: String): Int
    fun findByBrukerid(id: String): OppfølgingEntity?
    fun findByAktoerid(id: String): OppfølgingEntity?

    @Modifying
    @Query("DELETE FROM OppfølgingEntity o WHERE o.id = :id")
    fun deleteByUUID(id: UUID): Int
}


