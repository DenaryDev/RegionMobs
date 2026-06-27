/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit.mob;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.region.Region;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.EntityType;

import java.util.List;

/**
 * @author DenaryDev
 * @since 17:32 16.01.2024
 */
public final class MobList extends Mob {
    @Override
    protected String subName() {
        return "list";
    }

    @Override
    public String info() {
        return Config.messages().commands.edit.mob.list.info;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return root().executes(context -> {
            final CommandSourceStack source = context.getSource();

            final Region region = region(context);
            if (region == null) return 1;

            final List<EntityType> mobs = region.mobs();
            if (mobs.isEmpty()) {
                sendMessage(source, Config.messages().commands.edit.mob.list.empty, Placeholder.unparsed("id", region.id()));
                return 1;
            }

            sendMessage(source, Config.messages().commands.edit.mob.list.header, Placeholder.unparsed("id", region.id()));
            mobs.forEach(mob -> sendMessage(source, Config.messages().commands.edit.mob.list.entry,
                Placeholder.unparsed("type", mob.name().toLowerCase())));

            return 1;
        });
    }
}
