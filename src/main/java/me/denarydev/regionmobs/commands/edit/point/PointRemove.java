/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit.point;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.region.Region;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author DenaryDev
 * @since 16:59 13.01.2024
 */
public final class PointRemove extends Point {

    @Override
    protected String subName() {
        return "remove";
    }

    @Override
    public String info() {
        return Config.messages().commands.edit.point.remove.info;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return root().executes(context -> {
            final CommandSourceStack source = context.getSource();

            final Player player = player(source);
            if (player == null) return 1;

            final Region region = region(context);
            if (region == null) return 1;

            final Location point = playerLocationNoRot(player);

            if (!region.hasPoint(point)) {
                sendMessage(source, Config.messages().commands.edit.point.remove.notFound);
                return 1;
            }

            region.removePoint(point);
            if (saveFailed(source, region)) return 1;

            sendMessage(source, Config.messages().commands.edit.point.remove.success,
                Placeholder.unparsed("id", region.id()),
                Placeholder.unparsed("x", String.valueOf(Math.round(point.getX()))),
                Placeholder.unparsed("y", String.valueOf(Math.round(point.getY()))),
                Placeholder.unparsed("z", String.valueOf(Math.round(point.getZ())))
            );

            return 1;
        });
    }
}
