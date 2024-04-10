/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.denarydev.regionmobs.RegionMobsPlugin;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.command.VanillaCommandWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        commands.add(new About());
        commands.add(new Reload());
        commands.add(new Create());
        commands.add(new Delete());
        commands.add(new Enable());
        commands.add(new Disable());
        commands.add(new Particles());
        commands.add(new HelpEdit());
        // Point
        commands.add(new PointAdd());
        commands.add(new PointRemove());
        // Mob
        commands.add(new MobAdd());
        commands.add(new MobRemove());
        commands.add(new MobList());
    }

    public void registerCommands() {
        final var builder = literal("regionmobs");
        registerSubCommands(builder, commands);

        final var wrapper = new VanillaCommandWrapper(MinecraftServer.getServer().getCommands(), builder.build());
        wrapper.setAliases(List.of("rmobs"));
        Bukkit.getServer().getCommandMap().register(RegionMobsPlugin.instance().getName().toLowerCase(Locale.ROOT), wrapper);
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
