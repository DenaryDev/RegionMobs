/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands.edit.mob;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.rafaelka.regionmobs.Settings;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author DenaryDev
 * @since 17:32 16.01.2024
 */
public class MobList extends Mob {
    @Override
    protected String subName() {
        return "list";
    }

    @Override
    public String info() {
        return Settings.messages().commands.edit.mob.list.info;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return root().executes(context -> {
            final var source = context.getSource();

            final var region = region(context);
            if (region == null) return 1;

            final var mobs = region.mobs();
            if (mobs.isEmpty()) {
                sendMessage(source, Settings.messages().commands.edit.mob.list.empty, Placeholder.unparsed("id", region.id()));
                return 1;
            }

            sendMessage(source, Settings.messages().commands.edit.mob.list.header, Placeholder.unparsed("id", region.id()));
            mobs.forEach(mob -> sendMessage(source, Settings.messages().commands.edit.mob.list.entry,
                Placeholder.unparsed("type", mob.name().toLowerCase())));

            return 1;
        });
    }
}
