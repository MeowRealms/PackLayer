package io.th0rgal.packsmanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.packet.RemoveResourcePack;
import com.velocitypowered.proxy.protocol.packet.ResourcePackRequest;
import io.github._4drian3d.vpacketevents.api.event.PacketSendEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Plugin(
        id = "packlayer",
        name = "PackLayer",
        version = "${version}",
        description = "${description}",
        authors = {"th0rgal", "xiaozhangup"},
        dependencies = {
                @Dependency(id = "vpacketevents")
        }
)
public class PackLayer {
    private final Map<UUID, String> oldHashes = new HashMap<>();

    @Inject
    public PackLayer(ProxyServer proxyServer, Logger logger) {
        logger.info("PackLayer Loaded!");
    }

    @Subscribe
    public void onPacketSend(PacketSendEvent event) {
        final MinecraftPacket packet = event.getPacket();
        final UUID uuid = event.getPlayer().getUniqueId();
        int protocol = event.getPlayer().getProtocolVersion().getProtocol();

        if (packet instanceof RemoveResourcePack removeResourcePack) {
            if (protocol >= 764) { // 在 1.20.3+ 的服务端似乎会发一个这玩意, 不取消依然会导致客户端重载
                event.setResult(ResultedEvent.GenericResult.denied());
            }
        }

        if (packet instanceof ResourcePackRequest resourcePackRequest) {
            if (protocol == 764) return; // 1.20.2 没救了

            if (oldHashes.get(uuid).equals(resourcePackRequest.getHash())) {
                event.setResult(ResultedEvent.GenericResult.denied());
                return;
            }
            oldHashes.put(uuid, resourcePackRequest.getHash());
        }
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        oldHashes.remove(uuid);
    }
}