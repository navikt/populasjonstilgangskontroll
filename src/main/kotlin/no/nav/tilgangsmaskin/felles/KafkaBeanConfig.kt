package no.nav.tilgangsmaskin.felles

import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import tools.jackson.databind.json.JsonMapper

@Configuration
class KafkaBeanConfig {

    @Bean
    fun kafkaConsumerFactoryCustomizer(mapper: JsonMapper) =
        DefaultKafkaConsumerFactoryCustomizer {
            it.setValueDeserializerSupplier {
                ErrorHandlingDeserializer(JacksonJsonDeserializer(mapper))
            }
        }
}

