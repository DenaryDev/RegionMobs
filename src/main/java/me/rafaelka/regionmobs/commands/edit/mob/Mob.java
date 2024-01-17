/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands.edit.mob;

import com.mojang.brigadier.context.CommandContext;
import me.rafaelka.regionmobs.Settings;
import me.rafaelka.regionmobs.commands.edit.Edit;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
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
        final var s = string(context, "type");
        try {
            return EntityType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendMessage(context.getSource(), Settings.messages().commands.edit.mob.unknown, Placeholder.unparsed("type", s));
            return null;
        }
    }
}
