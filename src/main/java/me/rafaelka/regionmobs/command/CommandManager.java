/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.rafaelka.regionmobs.commands.Command;
import me.rafaelka.regionmobs.commands.Create;
import me.rafaelka.regionmobs.commands.Delete;
import me.rafaelka.regionmobs.commands.Disable;
import me.rafaelka.regionmobs.commands.Enable;
import me.rafaelka.regionmobs.commands.Particles;
import me.rafaelka.regionmobs.commands.Reload;
import me.rafaelka.regionmobs.commands.edit.EditWorld;
import me.rafaelka.regionmobs.commands.edit.mob.MobAdd;
import me.rafaelka.regionmobs.commands.edit.mob.MobList;
import me.rafaelka.regionmobs.commands.edit.mob.MobRemove;
import me.rafaelka.regionmobs.commands.edit.point.PointAdd;
import me.rafaelka.regionmobs.commands.edit.point.PointRemove;
import me.rafaelka.regionmobs.commands.help.HelpEdit;
import me.rafaelka.regionmobs.commands.help.HelpAll;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

/**
 * @author DenaryDev
 * @since 21:15 12.01.2024
 */
public class CommandManager {

    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        // Root
        commands.add(new HelpAll());
        commands.add(new Reload());
        commands.add(new Create());
        commands.add(new Delete());
        commands.add(new Enable());
        commands.add(new Disable());
        commands.add(new Particles());
        commands.add(new HelpEdit());
        // World
        commands.add(new EditWorld());
        // Point
        commands.add(new PointAdd());
        commands.add(new PointRemove());
        // Mob
        commands.add(new MobAdd());
        commands.add(new MobRemove());
        commands.add(new MobList());
    }

    public void registerCommands() {
        final var dispatcher = MinecraftServer.getServer().getCommands().getDispatcher();

        final var cmd = literal("regionmobs");
        registerSubCommands(cmd, commands);

        final var node = dispatcher.register(cmd);
        dispatcher.register(literal("rmobs").redirect(node));
    }

    public List<Command> commands() {
        return commands;
    }

    private void registerSubCommands(LiteralArgumentBuilder<CommandSourceStack> builder, List<Command> commands) {
        for (final var sub : commands) {
            final var cmd = sub.command();
            builder.then(cmd);
            if (!sub.aliases().isEmpty()) {
                for (final var alias : sub.aliases()) {
                    builder.then(literal(alias).redirect(cmd.build()));
                }
            }
        }
    }
}