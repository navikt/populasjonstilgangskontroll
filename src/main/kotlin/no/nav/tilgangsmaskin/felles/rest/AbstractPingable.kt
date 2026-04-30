package no.nav.tilgangsmaskin.felles.rest

abstract class AbstractPingable(cfg: AbstractRestConfig, private val doPing: () -> Any?) : Pingable {
    override val name = cfg.name
    override val pingEndpoint = cfg.pingEndpoint
    override fun ping() = doPing()
}
