package mcjty.rftoolsdim.modules.workbench.network;

import mcjty.rftoolsdim.modules.workbench.client.GuiWorkbench;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPatternToClient {

    private final String[] pattern;

    public PacketPatternToClient(String[] pattern) {
        this.pattern = pattern;
    }

    public PacketPatternToClient(PacketBuffer buf) {
        int size = buf.readInt();
        pattern = new String[size];
        for (int i = 0 ; i < size ; i++) {
            pattern[i] = buf.readUtf(32767);
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(pattern.length);
        for (String p : pattern) {
            buf.writeUtf(p);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            GuiWorkbench.setPattern(pattern);
        });
        ctx.setPacketHandled(true);
    }
}
