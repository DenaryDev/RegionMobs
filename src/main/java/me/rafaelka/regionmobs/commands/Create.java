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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 2:42 13.01.2024
 */
public class Create extends Command {

    @Override
    public String name() {
        return "create";
    }

    @Override
    public String info() {
        return Settings.messages().commands.create.info;
    }

    @Override
    public String args() {
        return "<id>";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return root().then(argument("id", word())
            .executes(context -> {
                final var source = context.getSource();

                if (serverPlayer(source) == null) return 1;

                final var id = string(context, "id").toLowerCase();
                if (plugin.regionManager().hasRegion(id)) {
                    sendMessage(source, Settings.messages().commands.create.exists);
                    return 1;
                }

                if (!plugin.regionManager().createRegion(id)) {
                    sendMessage(source, Settings.messages().commands.create.error);
                    return 1;
                }

                final var click = miniMessage(Settings.messages().commands.create.click,
                    Placeholder.unparsed("id", id),
                    Placeholder.unparsed("label", "rmobs")
                )
                    .hoverEvent(miniMessage(Settings.messages().commands.create.clickHover))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/rmobs edit " + id));
                sendMessage(source, Settings.messages().commands.create.success,
                    Placeholder.unparsed("id", id),
                    Placeholder.unparsed("label", "rmobs"),
                    Placeholder.component("click", click)
                );

                return 1;
            })
        );
    }
}
