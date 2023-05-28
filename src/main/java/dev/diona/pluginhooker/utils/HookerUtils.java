package dev.diona.pluginhooker.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

public class HookerUtils {

//    private static Field channelPipelineField;

    public static final AttributeKey<List<Consumer<Player>>> HANDLER_REPLACEMENT_FUNCTIONS
            = AttributeKey.valueOf("HANDLER_REPLACEMENT_FUNCTION");

    private static final Field pluginsField;

    static {

        try {
            pluginsField = Server.getInstance().getPluginManager().getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Plugin> getServerPlugins() {
        try {
            return (List<Plugin>) pluginsField.get(Server.getInstance().getPluginManager());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Player getPlayerByChannelContext(Object ctx) {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            ChannelPipeline pipeline = NMSUtils.getPipelineByPlayer(player);
            for (String name : pipeline.names()) {
                if (pipeline.context(name) != ctx) continue;
                return player;
            }
        }
        return null;
    }

    public static void addToOutList(Object msg, List<Object> out) {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            if (byteBuf.isReadable())
                out.add(byteBuf.retain());
        } else {
            out.add(msg);
        }
    }
}
