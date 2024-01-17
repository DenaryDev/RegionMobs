/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.rafaelka.regionmobs.Settings;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 2:42 13.01.2024
 */
public class Delete extends Command {
    @Override
    public String name() {
        return "delete";
    }

    @Override
    public String info() {
        return Settings.messages().commands.delete.info;
    }

    @Override
    public String args() {
        return "<id>";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return root().then(argument("id", word())
            .suggests(super::suggestRegion)
            .executes(context -> {
                final var source = context.getSource();
                final var region = region(context);
                if (region == null) return 1;

                if (!plugin.regionManager().deleteRegion(region)) {
                    sendMessage(source, Settings.messages().commands.delete.error, Placeholder.unparsed("id", region.id()));
                    return 1;
                }

                sendMessage(source, Settings.messages().commands.delete.success, Placeholder.unparsed("id", region.id()));

                return 1;
            })
        );
    }
}
