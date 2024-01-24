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
import me.denarydev.regionmobs.RegionMobsPlugin;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author DenaryDev
 * @since 22:43 12.01.2024
 */
public class Reload extends Command {
    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String info() {
        return Config.messages().commands.reload.info;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return root()
            .executes(context -> {
                RegionMobsPlugin.instance().reload();
                sendMessage(context.getSource(), Config.messages().commands.reload.success);
                return 1;
            });
    }
}
