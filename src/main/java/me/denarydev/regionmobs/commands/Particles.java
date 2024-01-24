/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.denarydev.regionmobs.Config;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author DenaryDev
 * @since 22:38 15.01.2024
 */
public class Particles extends Command {
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
            final var source = context.getSource();

            final var serverPlayer = serverPlayer(source);
            if (serverPlayer == null) return 1;
            final var player = serverPlayer.getBukkitEntity();

            if (plugin.particleManager().shown(player)) {
                plugin.particleManager().hide(player);
                sendMessage(source, Config.messages().commands.particles.hidden);
            } else {
                plugin.particleManager().show(player);
                sendMessage(source, Config.messages().commands.particles.shown);
            }

            return 1;
        });
    }
}
