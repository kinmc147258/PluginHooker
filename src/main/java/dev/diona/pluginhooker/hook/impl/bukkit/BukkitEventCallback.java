package dev.diona.pluginhooker.hook.impl.bukkit;

import cn.nukkit.event.Event;
import cn.nukkit.plugin.Plugin;
import lombok.Getter;

import java.util.function.BiPredicate;

public class BukkitEventCallback {

    @Getter
    private static BukkitEventCallback instance;

    private final BiPredicate<Plugin, Event> callback;

    public BukkitEventCallback(BiPredicate<Plugin, Event> callback) {
        instance = this;
        this.callback = callback;
    }

    public boolean onCallEvent(Plugin plugin, Event event) {
        return callback.test(plugin, event);
    }
}
