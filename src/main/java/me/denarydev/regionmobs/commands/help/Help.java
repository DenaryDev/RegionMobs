/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.help;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.commands.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author DenaryDev
 * @since 1:38 16.01.2024
 */
public abstract class Help extends Command {
    protected static final int PAGE_SIZE = 8;

    protected void sendHelp(CommandSourceStack source, String label, List<Command> commands, int page) {
        sendHelp(source, label, commands, page, null);
    }

    protected void sendHelp(CommandSourceStack source, String label, List<Command> commands, int page, @Nullable String regionId) {
        commands = commands.stream()
            .filter(c -> c.permission().isEmpty() || source.getSender().hasPermission(c.permission()))
            .toList();

        if (commands.isEmpty()) {
            sendMessage(source, Config.messages().commands.help.empty);
            return;
        }

        int lastPage = commands.size() / PAGE_SIZE;
        if (commands.size() % PAGE_SIZE != 0) lastPage++;

        if (page <= 0 || page > lastPage) {
            sendMessage(source, Config.messages().commands.help.page.notFound);
            return;
        }

        commands = commands.subList((page - 1) * PAGE_SIZE, Math.min(commands.size(), page * PAGE_SIZE));

        sendMessage(source, Config.messages().commands.help.list.header,
            Placeholder.unparsed("current", String.valueOf(page)),
            Placeholder.unparsed("total", String.valueOf(lastPage))
        );

        for (final Command sub : commands) {
            final String suggest = (regionId != null ? sub.suggest().replace("<id>", regionId) : sub.suggest()) + (sub.args().isBlank() ? "" : " ");
            source.getSender().sendMessage(miniMessage(Config.messages().commands.help.list.entry,
                    Placeholder.unparsed("label", label),
                    Placeholder.unparsed("command", suggest + sub.args()),
                    Placeholder.unparsed("info", sub.info())
                )
                    .hoverEvent(miniMessage(Config.messages().commands.help.list.entryHover))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, ClickEvent.Payload.string('/' + label + ' ' + suggest)))
            );
        }

        final String previousColor = page == 1 ? Config.messages().commands.help.page.color.inactive : Config.messages().commands.help.page.color.active;
        final Component previousComponent = miniMessage(Config.messages().commands.help.page.previous, Placeholder.parsed("color", previousColor));
        final Component previous = page == 1 ? previousComponent
            : previousComponent
              .hoverEvent(miniMessage(Config.messages().commands.help.page.hover))
              .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, ClickEvent.Payload.string('/' + label + " help " + (page - 1))));

        final String nextColor = page == lastPage ? Config.messages().commands.help.page.color.inactive : Config.messages().commands.help.page.color.active;
        final Component nextComponent = miniMessage(Config.messages().commands.help.page.next, Placeholder.parsed("color", nextColor));
        final Component next = page == lastPage ? nextComponent
            : nextComponent
              .hoverEvent(miniMessage(Config.messages().commands.help.page.hover))
              .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, ClickEvent.Payload.string('/' + label + " help " + (page + 1))));

        sendMessage(source, Config.messages().commands.help.list.footer,
            Placeholder.component("previous", previous), Placeholder.component("next", next));
    }
}
