package no.nav.tilgangsmaskin.felles.cache

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.StringRedisSerializer

private const val CHANNEL_EXPIRED = "__keyevent@0__:expired"
private const val CHANNEL_DELETED = "__keyevent@0__:del"

@Configuration
@ConditionalOnGCP
@NoCoverageAnalysis
class ValkeyEventListeningCacheConfig(
    private val cf: RedisConnectionFactory,
    private val oppfrisker: ValkeyEventListeningCacheOppfrisker,
) {

    @Bean
    fun valkeyEventListenerContainer(eventListener: MessageListener) =
        RedisMessageListenerContainer().apply {
            setConnectionFactory(cf)
            isAutoStartup = false
            addMessageListener(eventListener, ChannelTopic(CHANNEL_EXPIRED))
            addMessageListener(eventListener, ChannelTopic(CHANNEL_DELETED))
        }

    @Bean
    fun valkeyEventListener(): MessageListener =
        MessageListener { message: Message, _: ByteArray? ->
            StringRedisSerializer().deserialize(message.body)
                ?.let(::CacheNøkkel)
                ?.let(oppfrisker::onEvent)
        }
}

