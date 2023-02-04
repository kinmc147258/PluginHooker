package io.github.dionatestserver.pluginhooker.hook.impl.netty.channelhandler;

import io.github.dionatestserver.pluginhooker.PluginHooker;
import io.github.dionatestserver.pluginhooker.config.ConfigPath;
import io.github.dionatestserver.pluginhooker.events.NettyCodecEvent;
import io.github.dionatestserver.pluginhooker.player.DionaPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.List;

public class EncoderWrapper extends MessageToMessageEncoder<Object> {

    private final static Method encoderMethod;

    @ConfigPath("hook.netty.call-event")
    public static boolean callEvent;

    static {
        PluginHooker.getConfigManager().loadConfig(EncoderWrapper.class);
        try {
            encoderMethod = MessageToMessageEncoder.class
                    .getDeclaredMethod("encode", ChannelHandlerContext.class, Object.class, List.class);
            encoderMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final MessageToMessageEncoder<?> encoder;
    private final Plugin plugin;

    private final DionaPlayer dionaPlayer;

    public EncoderWrapper(MessageToMessageEncoder<?> encoder, Plugin plugin, Player player) {
        this.encoder = encoder;
        this.plugin = plugin;
        this.dionaPlayer = PluginHooker.getPlayerManager().getDionaPlayer(player);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (dionaPlayer.getEnabledPlugins().contains(plugin)) {
            if (!callEvent) {
                encoderMethod.invoke(encoder, ctx, msg, out);
                return;
            }
            NettyCodecEvent nettyCodecEvent = new NettyCodecEvent(plugin, dionaPlayer, msg, true);
            Bukkit.getPluginManager().callEvent(nettyCodecEvent);
            if (nettyCodecEvent.isCancelled()) {
                addToOutList(msg, out);
            } else {
                encoderMethod.invoke(encoder, ctx, msg, out);
            }
        } else {
            addToOutList(msg, out);
        }
    }

    private void addToOutList(Object msg, List<Object> out) {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            if (byteBuf.isReadable())
                out.add(byteBuf.retain());
        } else {
            out.add(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        throwable.printStackTrace();
    }
}
