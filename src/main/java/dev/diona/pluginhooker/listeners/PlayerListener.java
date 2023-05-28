package dev.diona.pluginhooker.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.scheduler.AsyncTask;
import dev.diona.pluginhooker.PluginHooker;
import dev.diona.pluginhooker.utils.HookerUtils;
import dev.diona.pluginhooker.utils.NMSUtils;
import io.netty.channel.Channel;

import java.util.List;
import java.util.function.Consumer;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        PluginHooker.getPlayerManager().addPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void postPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        try {
            Channel channel = NMSUtils.getChannelByPlayer(player);
            List<Consumer<Player>> list = channel.attr(HookerUtils.HANDLER_REPLACEMENT_FUNCTIONS).get();
            if (list == null) return;

            Server.getInstance().getScheduler().scheduleAsyncTask(PluginHooker.getInstance(), new AsyncTask() {
                @Override
                public void onRun() {
                    list.forEach(consumer -> consumer.accept(player));
                }
            });
        } catch (Exception ignore) {

        }
        //TODO: Nukkit自己写了一套基于UDP的发包协议，还没想好如何兼容
        PluginHooker.getPlayerManager().getDionaPlayer(player).setInitialized();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        PluginHooker.getPlayerManager().removePlayer(e.getPlayer());
    }
}
