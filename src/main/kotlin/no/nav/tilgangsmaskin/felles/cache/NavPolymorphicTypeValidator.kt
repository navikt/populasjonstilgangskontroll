package no.nav.tilgangsmaskin.felles.cache

import tools.jackson.databind.DatabindContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.jsontype.PolymorphicTypeValidator

class NavPolymorphicTypeValidator(private vararg val allowedPrefixes: String = arrayOf("no.nav.","java.", "kotlin.*")) : PolymorphicTypeValidator() {

    override fun validateBaseType(ctx: DatabindContext, baseType: JavaType) = validityFor(baseType.rawClass.name)

    override fun validateSubClassName(ctx: DatabindContext, baseType: JavaType, subClassName: String)  = validityFor(subClassName)

    override fun validateSubType(ctx: DatabindContext, baseType: JavaType, subType: JavaType) = validityFor(subType.rawClass.name)

    private fun validityFor(className: String) =
        if (allowedPrefixes.any { className.startsWith(it) }) Validity.ALLOWED else Validity.DENIED
}