package org.apollo.plugin.api

import org.apollo.pf4jbr.ScopedGrouping
import org.apollo.plugin.api.entity.InteractionOrder
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

/**
 * @author Khaled Abdeljaber
 */
@KotlinScript(
    displayName = "PluginDup", fileExtension = "plugin.kts", compilationConfiguration = PluginConfiguration1::class
)
class PluginDup(jar: File, scope: ScopedGrouping) : Plugin(jar, scope)
object PluginConfiguration1 : ScriptCompilationConfiguration({
    defaultImports(
        InteractionOrder::class.qualifiedName!!,
        "org.apollo.plugin.api.*",
    )
})