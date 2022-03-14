package org.apollo.plugin.api

import org.apollo.pf4jbr.ScopedGrouping
import org.apollo.plugin.api.table.DropTable
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

/**
 * @author Khaled Abdeljaber
 */
@KotlinScript(
    displayName = "NpcPluginDup", fileExtension = "npc.kts", compilationConfiguration = NpcPluginConfigurationDup::class
)
open class NpcPluginDup(
    jar: File, scope: ScopedGrouping
) : NpcPlugin(jar, scope)

object NpcPluginConfigurationDup : ScriptCompilationConfiguration({
    defaultImports(
        DropTable::class.qualifiedName!!,
    )
})