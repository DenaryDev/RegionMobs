/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands.edit.point;

import me.denarydev.regionmobs.commands.edit.Edit;

/**
 * @author DenaryDev
 * @since 16:44 13.01.2024
 */
public abstract class Point extends Edit {

    @Override
    public String name() {
        return "point";
    }
}
