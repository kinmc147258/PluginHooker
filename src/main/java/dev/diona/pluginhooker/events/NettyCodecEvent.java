package dev.diona.pluginhooker.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.Plugin;
import dev.diona.pluginhooker.player.DionaPlayer;
import lombok.Getter;

public class NettyCodecEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;

    @Getter
    private final Plugin plugin;
    @Getter
    private final DionaPlayer player;
    @Getter
    private final Object data;
    @Getter
    private final boolean outbound;

    public NettyCodecEvent(Plugin plugin, DionaPlayer player, Object data, boolean outbound) {
        this.plugin = plugin;
        this.player = player;
        this.data = data;
        this.outbound = outbound;
    }

    public HandlerList getHandlers() {
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
