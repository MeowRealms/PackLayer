package io.th0rgal.packsmanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerResourcePackSendEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.packet.RemoveResourcePack;
import com.velocitypowered.proxy.protocol.packet.ResourcePackRequest;
import com.velocitypowered.proxy.protocol.packet.ServerData;
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
    private final ProxyServer proxyServer;
    private final Logger logger;
    private static PackLayer INSTANCE;
    private final Map<UUID, String> old = new HashMap<>();

    @Inject
    public PackLayer(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        INSTANCE = this;
    }

    @Subscribe
    public void onPacketReceive(PacketSendEvent event) {
        final MinecraftPacket packet = event.getPacket();
        final UUID uuid = event.getPlayer().getUniqueId();
        int protocol = event.getPlayer().getProtocolVersion().getProtocol();

        if (packet instanceof RemoveResourcePack removeResourcePack) {
            if (protocol >= 764) {
                event.setResult(ResultedEvent.GenericResult.denied());
            }
        }

        if (packet instanceof ResourcePackRequest resourcePackRequest) {
            if (protocol == 764) return; // 1.20.2 没救了

            if (old.containsKey(uuid) && old.get(uuid).equals(resourcePackRequest.getHash())) {
                event.setResult(ResultedEvent.GenericResult.denied());
                return;
            }
            old.put(uuid, resourcePackRequest.getHash());
        }
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        old.remove(uuid);
    }
}
