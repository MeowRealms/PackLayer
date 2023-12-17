package io.th0rgal.packsmanager.velocity;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class PackRefresh implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        PackLayer.packUid = UUID.randomUUID();
        invocation.source().sendMessage(
                Component.text("New ResourcePack UUID: " + PackLayer.packUid)
        );
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("packlayer.refresh");
    }
}
