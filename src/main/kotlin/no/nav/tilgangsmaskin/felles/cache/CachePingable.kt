package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.rest.health.Pingable
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component
import java.net.URI

@Component
class CachePingable(
    private val cf: RedisConnectionFactory,
    @Value("\${spring.data.redis.host:localhost}") host: String,
    @Value("\${spring.data.redis.port:6379}") port: Int,
) : Pingable {

    override val pingEndpoint = URI.create("$host:$port")
    override val name = "Cache"

    override fun ping() =
        cf.connection.use {
            if (it.ping().equals(PONG, ignoreCase = true)) {
                Unit
            } else {
                error("$name ping failed")
            }
        }

    companion object {
        private const val PONG = "pong"
        const val VALKEY = "valkey"
    }
}

