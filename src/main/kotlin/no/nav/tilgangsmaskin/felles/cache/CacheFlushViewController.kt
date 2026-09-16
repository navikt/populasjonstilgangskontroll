package no.nav.tilgangsmaskin.felles.cache

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/cache")
class CacheFlushViewController {

    @GetMapping("flush")
    fun flushAnsatt(): String = "cache/flush"
}
