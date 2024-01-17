/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands.edit.point;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.rafaelka.regionmobs.Settings;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author DenaryDev
 * @since 16:59 13.01.2024
 */
public class PointRemove extends Point {

    @Override
    protected String subName() {
        return "remove";
    }

    @Override
    public String info() {
        return Settings.messages().commands.edit.point.remove.info;
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

            if (!region.hasPoint(point)) {
                sendMessage(source, Settings.messages().commands.edit.point.remove.notFound);
                return 1;
            }

            region.removePoint(point);
            if (saveFailed(source, region)) return 1;

            sendMessage(source, Settings.messages().commands.edit.point.remove.success,
                Placeholder.unparsed("id", region.id()),
                Placeholder.unparsed("x", String.valueOf(Math.round(point.getX()))),
                Placeholder.unparsed("y", String.valueOf(Math.round(point.getY()))),
                Placeholder.unparsed("z", String.valueOf(Math.round(point.getZ())))
            );

            return 1;
        });
    }
}
