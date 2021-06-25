package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.lib.network.ICommandHandler;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

// Client will send this packet to request dimlets from the server
public class PacketRequestDimlets {

    private final BlockPos pos;

    public PacketRequestDimlets(BlockPos pos) {
        this.pos = pos;
    }

    public PacketRequestDimlets(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                ICommandHandler commandHandler = (ICommandHandler) te;
                List<DimletClientHelper.DimletWithInfo> list = commandHandler.executeWithResultList(WorkbenchTileEntity.CMD_GETDIMLETS, TypedMap.EMPTY, Type.create(DimletClientHelper.DimletWithInfo.class));
                RFToolsDimMessages.INSTANCE.sendTo(new PacketSendDimletsToClient(pos, list), ctx.getSender().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.setPacketHandled(true);
    }
}
