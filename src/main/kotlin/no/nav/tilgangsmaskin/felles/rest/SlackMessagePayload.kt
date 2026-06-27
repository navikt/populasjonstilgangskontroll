package no.nav.tilgangsmaskin.felles.rest

data class SlackMessagePayload(
    val text: String? = null,
    val blocks: List<SlackBlock>? = null,
)

data class SlackBlock(
    val type: String,
    val text: SlackText? = null,
    val elements: List<SlackElement>? = null,
)

data class SlackText(
    val type: String = "mrkdwn",
    val text: String,
    val emoji: Boolean? = null,
)

sealed interface SlackElement

data class SlackMrkdwnElement(
    val type: String = "mrkdwn",
    val text: String,
) : SlackElement

data class SlackButtonElement(
    val type: String = "button",
    val text: SlackText,
    val style: String? = null,
    val value: String? = null,
) : SlackElement
