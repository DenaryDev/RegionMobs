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
import me.denarydev.regionmobs.commands.edit.Edit;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 1:53 16.01.2024
 */
public class HelpEdit extends Help {
    @Override
    public String name() {
        return "edit";
    }

    @Override
    public String info() {
        return Config.messages().commands.edit.info;
    }

    @Override
    public String args() {
        return "<id> [page]";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        final var subCommands = plugin.commandManager().commands().stream().filter(cmd -> cmd instanceof Edit).toList();
        return literal("edit")
            .requires(source -> source.isPlayer() && source.getBukkitSender().hasPermission("regionmobs.command.edit"))
            .then(argument("id", word())
                .suggests((ctx, builder) -> super.suggestRegion(builder))
                .executes(context -> {
                    final var region = region(context);
                    if (region == null) return 1;

                    sendHelp(context.getSource(), context.getInput().replace(" edit ", "").replace(region.id(), ""), subCommands, 1, region.id());
                    return 1;
                }).then(argument("page", integer())
                    .executes(context -> {
                        final var region = region(context);
                        if (region == null) return 1;

                        final var page = context.getArgument("page", Integer.class);
                        sendHelp(context.getSource(), context.getInput().replace(" edit ", "").replace(region.id(), ""), subCommands, page, region.id());
                        return 1;
                    })
                )
            );
    }
}
