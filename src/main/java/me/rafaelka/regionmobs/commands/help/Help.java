/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands.help;

import me.rafaelka.regionmobs.Settings;
import me.rafaelka.regionmobs.commands.Command;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author DenaryDev
 * @since 1:38 16.01.2024
 */
public abstract class Help extends Command {
    protected int pageSize = 8;

    protected void sendHelp(CommandSourceStack source, String label, List<Command> commands, int page) {
        sendHelp(source, label, commands, page, null);
    }

    protected void sendHelp(CommandSourceStack source, String label, List<Command> commands, int page, @Nullable String regionId) {
        commands = commands.stream()
            .filter(c -> c.permission().isEmpty() || source.getBukkitSender().hasPermission(c.permission()))
            .toList();

        if (commands.isEmpty()) {
            sendMessage(source, Settings.messages().commands.help.empty);
            return;
        }

        int lastPage = commands.size() / pageSize;
        if (commands.size() % pageSize != 0) lastPage++;

        if (page <= 0 || page > lastPage) {
            sendMessage(source, Settings.messages().commands.help.page.notFound);
            return;
        }

        commands = commands.subList((page - 1) * pageSize, Math.min(commands.size(), page * pageSize));

        sendMessage(source, Settings.messages().commands.help.list.header,
            Placeholder.unparsed("current", String.valueOf(page)),
            Placeholder.unparsed("total", String.valueOf(lastPage))
        );

        for (final var sub : commands) {
            final var suggest = (regionId != null ? sub.suggest().replace("<id>", regionId) : sub.suggest()) + (sub.args().isBlank() ? "" : " ");
            source.getBukkitSender().sendMessage(miniMessage(Settings.messages().commands.help.list.entry,
                    Placeholder.unparsed("label", label),
                    Placeholder.unparsed("command", suggest + sub.args()),
                    Placeholder.unparsed("info", sub.info())
                )
                    .hoverEvent(miniMessage(Settings.messages().commands.help.list.entryHover))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, '/' + label + ' ' + suggest))
            );
        }

        final var previousColor = page == 1 ? Settings.messages().commands.help.page.color.inactive : Settings.messages().commands.help.page.color.active;
        final var previousComponent = miniMessage(Settings.messages().commands.help.page.previous, Placeholder.parsed("color", previousColor));
        final var previous = page == 1 ? previousComponent
            : previousComponent
            .hoverEvent(miniMessage(Settings.messages().commands.help.page.hover))
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " help " + (page - 1)));

        final var nextColor = page == lastPage ? Settings.messages().commands.help.page.color.inactive : Settings.messages().commands.help.page.color.active;
        final var nextComponent = miniMessage(Settings.messages().commands.help.page.next, Placeholder.parsed("color", nextColor));
        final var next = page == lastPage ? nextComponent
            : nextComponent
            .hoverEvent(miniMessage(Settings.messages().commands.help.page.hover))
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " help " + (page + 1)));

        sendMessage(source, Settings.messages().commands.help.list.footer,
            Placeholder.component("previous", previous), Placeholder.component("next", next));
    }
}
