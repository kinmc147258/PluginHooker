package dev.diona.pluginhooker.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.Plugin;
import dev.diona.pluginhooker.player.DionaPlayer;
import lombok.Getter;

public class NukkitListenerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancel;

    @Getter
    private final Plugin plugin;
    @Getter
    private final Event event;
    @Getter
    private final DionaPlayer dionaPlayer;

    public NukkitListenerEvent(Plugin plugin, Event event) {
        this(plugin, event, null);
    }

    public NukkitListenerEvent(Plugin plugin, Event event, DionaPlayer dionaPlayer) {
        super();
        this.plugin = plugin;
        this.event = event;
        this.dionaPlayer = dionaPlayer;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
