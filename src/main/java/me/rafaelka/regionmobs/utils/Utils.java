/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.utils;

import org.bukkit.Location;

/**
 * @author DenaryDev
 * @since 16:39 16.01.2024
 */
public final class Utils {

    public static Location centerLocation(Location location) {
        return location.toCenterLocation().add(0, -0.5, 0);
    }
}
