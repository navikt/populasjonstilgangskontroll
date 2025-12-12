package no.nav.tilgangsmaskin.felles.cache

import org.springframework.data.repository.CrudRepository

interface RedisRepository : CrudRepository<String, Any> {}