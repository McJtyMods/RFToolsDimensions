package mcjty.rftoolsdim.modules.workbench.network;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.workbench.client.GuiWorkbench;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PacketPatternToClient(String[] pattern) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsDim.MODID, "pattern_to_client");

    public static PacketPatternToClient create(FriendlyByteBuf buf) {
        int size = buf.readInt();
        String[] pattern = new String[size];
        for (int i = 0 ; i < size ; i++) {
            pattern[i] = buf.readUtf(32767);
        }
        return new PacketPatternToClient(pattern);
    }

    public static PacketPatternToClient create(String[] p) {
        return new PacketPatternToClient(p);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(pattern.length);
        for (String p : pattern) {
            buf.writeUtf(p);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            GuiWorkbench.setPattern(pattern);
        });
    }
}
