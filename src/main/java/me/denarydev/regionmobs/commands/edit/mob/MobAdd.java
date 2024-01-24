/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit.mob;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.denarydev.regionmobs.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.bukkit.entity.EntityType;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 23:47 15.01.2024
 */
public class MobAdd extends Mob {
    @Override
    protected String subName() {
        return "add";
    }

    @Override
    public String args() {
        return "<type>";
    }

    @Override
    public String info() {
        return Config.messages().commands.edit.mob.add.info;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return root().then(argument("type", word())
            .suggests(((context, builder) -> SharedSuggestionProvider.suggest(Arrays.stream(EntityType.values())
                .map(EntityType::name)
                .map(String::toLowerCase)
                .toList(), builder)))
            .executes(context -> {
                final var source = context.getSource();

                final var region = region(context);
                if (region == null) return 1;
                final var entity = entityType(context);
                if (entity == null) return 1;

                if (region.hasMob(entity)) {
                    sendMessage(source, Config.messages().commands.edit.mob.add.already,
                        Placeholder.unparsed("id", region.id()),
                        Placeholder.unparsed("type", entity.name().toLowerCase())
                    );
                    return 1;
                }

                region.addMob(entity);
                if (saveFailed(source, region)) return 1;

                sendMessage(source, Config.messages().commands.edit.mob.add.success,
                    Placeholder.unparsed("id", region.id()),
                    Placeholder.unparsed("type", entity.name().toLowerCase())
                );

                return 1;
            })
        );
    }
}
