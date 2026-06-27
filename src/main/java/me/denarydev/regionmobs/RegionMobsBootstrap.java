/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.denarydev.regionmobs.command.CommandManager;
import org.jspecify.annotations.NonNull;

/**
 * @author DenaryDev
 * @since 19:38 21.12.2025
 */
@SuppressWarnings("UnstableApiUsage")
public final class RegionMobsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NonNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            CommandManager.registerCommands(event.registrar());
        });
    }
}
