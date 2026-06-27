/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.crystal.paper.utils.LocationUtils;
import me.denarydev.regionmobs.commands.Command;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 15:12 13.01.2024
 */
public abstract class Edit extends Command {

    protected abstract String subName();

    @Override
    public String permissionBase() {
        return super.permissionBase() + "edit.";
    }

    @Override
    public String permission() {
        return super.permission() + (subName().isEmpty() ? "" : '.' + subName());
    }

    @Override
    public String suggest() {
        return "edit <id> " + name() + (subName().isEmpty() ? "" : " " + subName());
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return literal("edit")
            .requires(source -> source.getSender() instanceof Player player && player.hasPermission("regionmobs.command.edit"))
            .then(argument("id", word())
                .suggests((ctx, builder) -> super.suggestRegion(builder))
                .then(literal(name())
                    .requires(super::checkPermission)
                    .then(subCommand())
                )
            );
    }

    protected abstract ArgumentBuilder<CommandSourceStack, ?> subCommand();

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> root() {
        return literal(subName());
    }

    @NotNull
    protected Location playerLocationNoRot(@NotNull Player player) {
        final Location location = LocationUtils.centerLocation(player.getLocation());
        location.setYaw(0);
        location.setPitch(0);

        return location;
    }
}
