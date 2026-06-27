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

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 17:29 16.01.2024
 */
public final class MobRemove extends Mob {
    @Override
    protected String subName() {
        return "remove";
    }

    @Override
    public String info() {
        return Config.messages().commands.edit.mob.remove.info;
    }

    @Override
    public String args() {
        return "<type>";
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return root().then(argument("type", word())
            .executes(context -> {
                final CommandSourceStack source = context.getSource();

                final Region region = region(context);
                if (region == null) return 1;
                final EntityType entity = entityType(context);
                if (entity == null) return 1;

                if (!region.hasMob(entity)) {
                    sendMessage(source, Config.messages().commands.edit.mob.remove.notFound,
                        Placeholder.unparsed("id", region.id()),
                        Placeholder.unparsed("type", entity.name().toLowerCase())
                    );
                    return 1;
                }

                region.removeMob(entity);
                if (saveFailed(source, region)) return 1;

                sendMessage(source, Config.messages().commands.edit.mob.remove.success,
                    Placeholder.unparsed("id", region.id()),
                    Placeholder.unparsed("type", entity.name().toLowerCase())
                );

                return 1;
            })
        );
    }
}
