package no.nav.tilgangsmaskin.felles.cache

import org.springframework.stereotype.Component
import kotlin.collections.indexOf

@Component
class CacheSlotCalculator(private val handler: CacheNøkkelHandler) {

    fun slotsFor(innslag: Map<String, Any>, cache: CachableConfig) =
        innslag.entries
            .groupBy { slotFor(handler.nøkkel(it.key, cache)) }
            .mapValues { it.value.associate { entry -> entry.toPair() } }


     fun slotFor(key: String): Int {
        val bytes = key.toByteArray()
        return slotFor(bytes)
    }

    private fun slotFor(key: ByteArray): Int {
        val hashKey = extractHashTag(key) ?: key
        var crc = 0
        hashKey.forEach { byte ->
            crc = ((crc shl 8) xor CRC16_TAB[((crc shr 8) xor (byte.toInt() and 0xFF)) and 0xFF]) and 0xFFFF
        }
        return crc % SLOT_COUNT
    }

    private fun extractHashTag(key: ByteArray): ByteArray? {
        val start = key.indexOf('{'.code.toByte())
        if (start == -1) return null

        val end = key.drop(start + 1).indexOf('}'.code.toByte())
            .takeIf { it != -1 }?.let { it + start + 1 } ?: -1

        if (end == -1 || end == start + 1) return null

        return key.copyOfRange(start + 1, end)
    }

    companion object {
        private val CRC16_TAB = IntArray(256) { i ->
            var crc = i shl 8
            repeat(8) {
                crc = if ((crc and 0x8000) != 0) (crc shl 1) xor 0x1021 else crc shl 1
            }
            crc and 0xFFFF
        }
        private const val SLOT_COUNT = 16384
    }
}