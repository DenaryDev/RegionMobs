/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.RegionMobsPlugin;
import me.denarydev.regionmobs.particle.ParticleManager;
import org.bukkit.entity.Player;

/**
 * @author DenaryDev
 * @since 22:38 15.01.2024
 */
public final class Particles extends Command {
    @Override
    public String name() {
        return "particles";
    }

    @Override
    public String info() {
        return Config.messages().commands.particles.info;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return root().executes(context -> {
            final CommandSourceStack source = context.getSource();

            final Player player = player(source);
            if (player == null) return 1;

            final ParticleManager particleManager = RegionMobsPlugin.instance().particleManager();
            if (particleManager.shown(player)) {
                particleManager.hide(player);
                sendMessage(source, Config.messages().commands.particles.hidden);
            } else {
                particleManager.show(player);
                sendMessage(source, Config.messages().commands.particles.shown);
            }

            return 1;
        });
    }
}
