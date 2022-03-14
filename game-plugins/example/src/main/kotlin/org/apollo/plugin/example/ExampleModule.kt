package org.apollo.plugin.example

import org.apollo.pf4jbr.P4fjModule
import org.apollo.pf4jbr.PluginDescriptor
import org.apollo.pf4jbr.ScopedGrouping
import java.io.File

/**
 * @author Khaled Abdeljaber
 */
@PluginDescriptor(
    name = "Example Plugin",
    description = "Show example of how to program a basic plugin",
    tags = ["example"],
    enabled = false // We don't want to actually load the plugin, just show the syntax
)
class ExampleModule(jar: File, scope: ScopedGrouping) : P4fjModule(jar, scope)