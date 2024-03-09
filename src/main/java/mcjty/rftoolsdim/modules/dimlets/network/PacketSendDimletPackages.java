package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

// Server will use this packet to send back dimlets to the client
public record PacketSendDimletPackages(Map<DimletKey, DimletSettings> dimlets) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsDim.MODID, "senddimletpackages");

    public PacketSendDimletPackages(Map<DimletKey, DimletSettings> dimlets) {
        this.dimlets = new HashMap<>(dimlets);
    }

    public static PacketSendDimletPackages create(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<DimletKey, DimletSettings> dimlets = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            DimletKey key = DimletKey.create(buf);
            DimletSettings settings = new DimletSettings(buf);
            dimlets.put(key, settings);
        }
        return new PacketSendDimletPackages(dimlets);
    }

    public static PacketSendDimletPackages create(Map<DimletKey, DimletSettings> collected) {
        return new PacketSendDimletPackages(collected);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(dimlets.size());
        for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
            entry.getKey().toBytes(buf);
            entry.getValue().toBytes(buf);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            RegistryAccess access = SafeClientTools.getClientWorld().registryAccess();
            DimletDictionary dictionary = DimletDictionary.get();
            for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
                dictionary.register(access, entry.getKey(), entry.getValue());
            }
        });
    }
}
