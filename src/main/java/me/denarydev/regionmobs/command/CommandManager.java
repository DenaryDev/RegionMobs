/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.denarydev.regionmobs.commands.About;
import me.denarydev.regionmobs.commands.Command;
import me.denarydev.regionmobs.commands.Create;
import me.denarydev.regionmobs.commands.Delete;
import me.denarydev.regionmobs.commands.Disable;
import me.denarydev.regionmobs.commands.Enable;
import me.denarydev.regionmobs.commands.Particles;
import me.denarydev.regionmobs.commands.Reload;
import me.denarydev.regionmobs.commands.edit.mob.MobAdd;
import me.denarydev.regionmobs.commands.edit.mob.MobList;
import me.denarydev.regionmobs.commands.edit.mob.MobRemove;
import me.denarydev.regionmobs.commands.edit.point.PointAdd;
import me.denarydev.regionmobs.commands.edit.point.PointRemove;
import me.denarydev.regionmobs.commands.help.HelpAll;
import me.denarydev.regionmobs.commands.help.HelpEdit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DenaryDev
 * @since 21:15 12.01.2024
 */
public final class CommandManager {

    private static final List<Command> subCommands = new ArrayList<>();

    static {
        // Root
        subCommands.add(new HelpAll());
        subCommands.add(new About());
        subCommands.add(new Reload());
        subCommands.add(new Create());
        subCommands.add(new Delete());
        subCommands.add(new Enable());
        subCommands.add(new Disable());
        subCommands.add(new Particles());
        subCommands.add(new HelpEdit());
        // Point
        subCommands.add(new PointAdd());
        subCommands.add(new PointRemove());
        // Mob
        subCommands.add(new MobAdd());
        subCommands.add(new MobRemove());
        subCommands.add(new MobList());
    }

    public static void registerCommands(Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("regionmobs");

        for (final Command sub : CommandManager.subCommands) {
            final ArgumentBuilder<CommandSourceStack, ?> cmd = sub.command();
            builder.then(cmd);

            if (!sub.aliases().isEmpty()) {
                for (final String alias : sub.aliases()) {
                    builder.then(Commands.literal(alias).redirect(cmd.build()));
                }
            }
        }

        commands.register(builder.build(), List.of("rmobs"));
    }

    public static List<Command> commands() {
        return subCommands;
    }
}
