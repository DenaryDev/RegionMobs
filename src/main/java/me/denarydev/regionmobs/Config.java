/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs;

import me.denarydev.crystal.config.BukkitConfigs;
import me.denarydev.crystal.config.CrystalConfigs;
import org.bukkit.Particle;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 1:53 12.01.2024
 */
public class Config {

    private static Settings settings;
    private static Messages messages;

    public static Settings settings() {
        return settings;
    }

    public static Messages messages() {
        return messages;
    }

    public static void load(Path path) throws ConfigurateException {
        final var serializers = BukkitConfigs.serializers();
        settings = CrystalConfigs.loadConfig(path.resolve("settings.conf"), serializers, Settings.class, false);
        messages = CrystalConfigs.loadConfig(path.resolve("messages.conf"), Messages.class, false);
    }

    @ConfigSerializable
    public static final class Settings {
        @Comment("Настройки появления мобов в областях")
        public MobSpawn mobSpawn = new MobSpawn();
        @Comment("Настройки частиц, отображаемых командой /rmobs particles")
        public Particles particles = new Particles();

        @ConfigSerializable
        public static final class MobSpawn {
            @Comment("Интервал между попытками создать стек мобов")
            public int interval = 300;
            @Comment("Минимальный размер пачки мобов по умолчанию.\nТак же есть в конфиге каждого региона.")
            public int minCapSize = 2;
            @Comment("Максимальный размер пачки мобов по умолчанию.\nТак же есть в конфиге каждого региона.")
            public int maxCapSize = 8;
            @Comment("Если расстояние между мобом и игроком больше этого значения, моб деспавнится.\nТак же есть в конфиге каждого региона.")
            public int despawnDistance = 48;
            @Comment("Радиус области вокруг игрока, в которой могут появляться мобы.\nТак же есть в конфиге каждого региона.")
            public int maxDistance = 32;
            @Comment("Будут ли появляться детёныши мобов.\nТак же есть в конфиге каждого региона.")
            public boolean allowBabies = true;
        }

        @ConfigSerializable
        public static final class Particles {
            @Comment("""
                Тип частиц, все доступные типы: https://jd.papermc.io/paper/1.20/org/bukkit/Particle.html
                """)
            public Particle type = Particle.VILLAGER_HAPPY;
            @Comment("Интервал между попытками создания частиц")
            public int interval = 10;
            @Comment("Количество частиц, появляющихся над блоком за одну попытку")
            public int count = 4;
        }
    }

    @ConfigSerializable
    public static final class Messages {
        public Replacements replacements = new Replacements();
        public Errors errors = new Errors();
        public Commands commands = new Commands();

        @ConfigSerializable
        public static final class Replacements {
            public String prefix = "<yellow>[Система] <gray>Мобы: <reset>";
        }

        @ConfigSerializable
        public static final class Errors {
            public Region region = new Region();

            public String notPlayer = "<prefix>Эту команду могут использовать только игроки!";

            @ConfigSerializable
            public static final class Region {
                public String notFound = "<prefix>Область с таким ID не найдена!";
                public String saveError = "<prefix>Не удалось сохранить область!";
            }
        }

        @ConfigSerializable
        public static final class Commands {
            public Help help = new Help();
            public Reload reload = new Reload();
            public Create create = new Create();
            public Delete delete = new Delete();
            public Enable enable = new Enable();
            public Disable disable = new Disable();
            public Particles particles = new Particles();
            public Edit edit = new Edit();

            @ConfigSerializable
            public static final class Help {
                public List list = new List();
                public Page page = new Page();
                public String info = "Показывает все доступные команды на указанной странице";
                public String empty = "У вас нет доступа к каким-либо командам!";

                @ConfigSerializable
                public static final class List {
                    public String header = "<prefix>Доступные команды (<yellow><current><gray>/<yellow><total><white>):";
                    public String entry = "<dark_gray><b>*</b> <yellow>/<label> <command> <gray>- <white><info>";
                    public String entryHover = "<gray>Нажмите, чтобы вставить в строку ввода";
                    public String footer = "<previous> <gray>|<reset> <next>";
                }

                @ConfigSerializable
                public static final class Page {
                    public Color color = new Color();

                    public String next = "<color>[Вперёд]<reset> <gold>>>";
                    public String previous = "<gold><< <color>[Назад]<reset>";
                    public String hover = "<gray>Нажмите, чтобы перейти";
                    public String notFound = "<prefix>Страница с таким номером не найдена!";

                    @ConfigSerializable
                    public static final class Color {
                        public String active = "<yellow><u><b>";
                        public String inactive = "<gray><b>";
                    }
                }
            }

            @ConfigSerializable
            public static final class Reload {
                public String info = "Перезагружает конфигурацию плагина";
                public String success = "<prefix>Конфигурация плагина перезагружена.";
            }

            @ConfigSerializable
            public static final class Create {
                public String info = "Создаёт область появления мобов";
                public String success = "<prefix>Вы создали область <yellow><id><white>! Команды для настройки: <yellow><click><white>, после настройки включите область командой <yellow>/rmobs enable <id><white>.";
                public String error = "<prefix>Не удалось создать область из-за непредвиденной ошибки, в консоли больше информации об этом.";
                public String exists = "<prefix>Область с таким ID уже существует.";
                public String click = "<u>/rmobs edit <id></u>";
                public String clickHover = "<gray>Нажмите, чтобы показать команды";
            }

            @ConfigSerializable
            public static final class Delete {
                public String info = "Удаляет область появления мобов";
                public String success = "<prefix>Область <yellow><id><white> успешно удалена.";
                public String error = "<prefix>Не удалось удалить область из-за непредвиденной ошибки, в консоли больше информации об этом.";
            }

            @ConfigSerializable
            public static final class Enable {
                public String info = "Включает появление мобов в области";
                public String success = "<prefix>Появление мобов в области <yellow><id><white> включено.";
                public String already = "<prefix>В области <yellow><id><white> уже включено появление мобов.";
                public String incomplete = "<prefix>Чтобы включить появление мобов, нужно полностью настроить область, используйте <yellow>/rmobs edit <id><white> для просмотра команд настройки области.";
            }

            @ConfigSerializable
            public static final class Disable {
                public String info = "Отключает появление мобов в области";
                public String success = "<prefix>Появление мобов в области <yellow><id><white> отключено.";
                public String already = "<prefix>В области <yellow><id><white> уже отключено появление мобов.";
            }

            @ConfigSerializable
            public static final class Particles {
                public String info = "Переключает отображение частиц над точками появления мобов";
                public String shown = "<prefix>Частицы над точками появления мобов <green>показаны<white>.";
                public String hidden = "<prefix>Частицы над точками появления мобов <red>скрыты<white>.";
            }

            @ConfigSerializable
            public static final class Edit {
                public String info = "Выводит команды, необходимые для настройки области.";
                public Point point = new Point();
                public Mob mob = new Mob();

                @ConfigSerializable
                public static final class Point {
                    public Add add = new Add();
                    public Remove remove = new Remove();

                    @ConfigSerializable
                    public static final class Add {
                        public String info = "Добавляет точку появления мобов на вашей позиции";
                        public String success = "<prefix>Точка появления мобов создана на вашей позиции (<yellow><x><white>, <yellow><y><white>, <yellow><z><white>) в области <yellow><id><white>.";
                        public String already = "<prefix>На вашей позиции уже существует точка появления мобов в этой области.";
                    }

                    @ConfigSerializable
                    public static final class Remove {
                        public String info = "Удаляет точку появления мобов на вашей позиции";
                        public String success = "<prefix>Точка появления мобов удалена на вашей позиции (<yellow><x><white>, <yellow><y><white>, <yellow><z><white>) из области <yellow><id><white>.";
                        public String notFound = "<prefix>На вашей позиции нет точки появления мобов в этой области.";
                    }
                }

                @ConfigSerializable
                public static final class Mob {
                    public Add add = new Add();
                    public Remove remove = new Remove();
                    public List list = new List();
                    public String unknown = "<prefix>Тип моба <yellow><type><white> не найден.";

                    @ConfigSerializable
                    public static final class Add {
                        public String info = "Добавляет тип моба для появления в области";
                        public String success = "<prefix>Тип моба <yellow><type><white> добавлен в область <yellow><id><white>.";
                        public String already = "<prefix>Указанный тип моба уже добавлен в эту область.";
                    }

                    @ConfigSerializable
                    public static final class Remove {
                        public String info = "Удаляет тип моба из области";
                        public String success = "<prefix>Тип моба <yellow><type><white> удалён из области <yellow><id><white>.";
                        public String notFound = "<prefix>Указанный тип моба не найден в этой области.";
                    }

                    @ConfigSerializable
                    public static final class List {
                        public String info = "Показывает список всех добавленных в область мобов";
                        public String empty = "<prefix>В область ещё не добавлено ни одного типа моба";
                        public String header = "<prefix>Список мобов в области <yellow><id><white>:";
                        public String entry = "<dark_gray><b>*</b> <yellow><type><white>";
                    }
                }
            }
        }
    }
}
