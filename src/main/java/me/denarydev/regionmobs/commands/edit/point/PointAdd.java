/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit.point;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.denarydev.regionmobs.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author DenaryDev
 * @since 16:52 13.01.2024
 */
public class PointAdd extends Point {

    @Override
    protected String subName() {
        return "add";
    }

    @Override
    public String info() {
        return Config.messages().commands.edit.point.add.info;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return root().executes(context -> {
            final var source = context.getSource();

            final var player = serverPlayer(source);
            if (player == null) return 1;

            final var region = region(context);
            if (region == null) return 1;

            final var point = playerLocationNoRot(player);

            if (region.hasPoint(point)) {
                sendMessage(source, Config.messages().commands.edit.point.add.already);
                return 1;
            }

            region.addPoint(point);
            if (saveFailed(source, region)) return 1;

            sendMessage(source, Config.messages().commands.edit.point.add.success,
                Placeholder.unparsed("id", region.id()),
                Placeholder.unparsed("x", String.valueOf(point.getX())),
                Placeholder.unparsed("y", String.valueOf(point.getY())),
                Placeholder.unparsed("z", String.valueOf(point.getZ()))
            );

            return 1;
        });
    }
}
