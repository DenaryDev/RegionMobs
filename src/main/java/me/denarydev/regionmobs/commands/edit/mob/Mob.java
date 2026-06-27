/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit.mob;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.commands.edit.Edit;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.EntityType;

/**
 * @author DenaryDev
 * @since 23:46 15.01.2024
 */
public abstract class Mob extends Edit {

    @Override
    public String name() {
        return "mob";
    }

    protected EntityType entityType(CommandContext<CommandSourceStack> context) {
        final String type = string(context, "type");
        try {
            return EntityType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendMessage(context.getSource(), Config.messages().commands.edit.mob.unknown, Placeholder.unparsed("type", type));
            return null;
        }
    }
}
