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

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 17:29 16.01.2024
 */
public class MobRemove extends Mob {
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
            //TODO: Suggestions error: java.lang.IllegalArgumentException: No such argument 'id' exists on this command
            //.suggests((context, builder) -> {
            //    final var region = region(context);
            //    if (region == null) return Suggestions.empty();
            //    return SharedSuggestionProvider.suggest(region.mobs().stream()
            //        .map(EntityType::name)
            //        .map(String::toLowerCase)
            //        .toList(), builder);
            //})
            .executes(context -> {
                final var source = context.getSource();

                final var region = region(context);
                if (region == null) return 1;
                final var entity = entityType(context);
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
