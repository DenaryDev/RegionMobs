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
 * @since 23:11 15.01.2024
 */
public class Enable extends Command {
    @Override
    public String name() {
        return "enable";
    }

    @Override
    public String info() {
        return Settings.messages().commands.enable.info;
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

                if (region.incomplete()) {
                    sendMessage(source, Settings.messages().commands.enable.incomplete, Placeholder.unparsed("id", region.id()));
                    return 1;
                }

                if (region.enabled()) {
                    sendMessage(source, Settings.messages().commands.enable.already, Placeholder.unparsed("id", region.id()));
                    return 1;
                }

                region.enabled(true);
                if (saveFailed(source, region)) return 1;

                sendMessage(source, Settings.messages().commands.enable.success, Placeholder.unparsed("id", region.id()));

                return 1;
            })
        );
    }
}
