/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.help;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.denarydev.regionmobs.Config;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

/**
 * @author DenaryDev
 * @since 23:13 12.01.2024
 */
public class HelpAll extends Help {

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String info() {
        return Config.messages().commands.help.info;
    }

    @Override
    public String args() {
        return "<page>";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        final var subCommands = plugin.commandManager().commands();
        return root()
            .executes(context -> {
                sendHelp(context.getSource(), context.getInput().replace(" help", ""), subCommands, 1);
                return 1;
            }).then(argument("page", integer())
                .executes(context -> {
                    final var page = context.getArgument("page", Integer.class);
                    sendHelp(context.getSource(), context.getInput().replace(" help " + page, ""), subCommands, page);
                    return 1;
                }));
    }
}
