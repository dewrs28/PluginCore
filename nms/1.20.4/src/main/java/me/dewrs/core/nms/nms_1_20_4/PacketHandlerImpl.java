package me.dewrs.core.nms.nms_1_20_4;

import me.dewrs.core.packets.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHandlerImpl implements PacketHandler {
    @Override
    public void sendFakeBlock(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer handle = craftPlayer.getHandle();
        ServerGamePacketListenerImpl connection = handle.connection;

        BlockState blockState = Blocks.RED_WOOL.defaultBlockState();
        BlockPos blockPosition = new BlockPos(player.getLocation().getBlockX() + 1, player.getLocation().getBlockY(), player.getLocation().getBlockZ() + 1);
        ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(blockPosition, blockState);

        connection.send(packet);
    }
}
