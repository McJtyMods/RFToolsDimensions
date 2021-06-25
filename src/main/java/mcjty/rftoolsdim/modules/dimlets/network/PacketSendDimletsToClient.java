package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.network.IClientCommandHandler;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// Server will use this packet to send back dimlets to the client
public class PacketSendDimletsToClient {

    private final BlockPos pos;
    private final List<DimletClientHelper.DimletWithInfo> dimlets;

    public PacketSendDimletsToClient(BlockPos pos, List<DimletClientHelper.DimletWithInfo> dimlets) {
        this.pos = pos;
        this.dimlets = new ArrayList<>(dimlets);
    }

    public PacketSendDimletsToClient(PacketBuffer buf) {
        pos = buf.readBlockPos();
        int size = buf.readInt();
        dimlets = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            short idx = buf.readShort();
            DimletType type = DimletType.values()[idx];
            String key = buf.readUtf(32767);
            DimletKey dimlet = new DimletKey(type, key);
            boolean craftable = buf.readBoolean();
            dimlets.add(new DimletClientHelper.DimletWithInfo(dimlet, craftable));
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(dimlets.size());
        for (DimletClientHelper.DimletWithInfo key : dimlets) {
            DimletKey dimlet = key.getDimlet();
            buf.writeShort(dimlet.getType().ordinal());
            buf.writeUtf(dimlet.getKey());
            buf.writeBoolean(key.isCraftable());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = McJtyLib.proxy.getClientWorld().getBlockEntity(pos);
            if (te instanceof IClientCommandHandler) {
                IClientCommandHandler clientCommandHandler = (IClientCommandHandler) te;
                if (!clientCommandHandler.receiveListFromServer(WorkbenchTileEntity.CLIENT_CMD_GETDIMLETS, dimlets, Type.create(DimletClientHelper.DimletWithInfo.class))) {
                    Logging.log("Command " + WorkbenchTileEntity.CLIENT_CMD_GETDIMLETS + " was not handled!");
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
