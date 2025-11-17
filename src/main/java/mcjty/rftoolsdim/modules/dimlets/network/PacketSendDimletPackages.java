package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

// Server will use this packet to send back dimlets to the client
public record PacketSendDimletPackages(Map<DimletKey, DimletSettings> dimlets) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "senddimletpackages");
    public static final CustomPacketPayload.Type<PacketSendDimletPackages> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketSendDimletPackages> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.dimlets.size());
                for (Map.Entry<DimletKey, DimletSettings> entry : packet.dimlets.entrySet()) {
                    entry.getKey().toBytes(buf);
                    entry.getValue().toBytes(buf);
                }
            },
            buf -> {
                int size = buf.readInt();
                Map<DimletKey, DimletSettings> dimlets = new HashMap<>(size);
                for (int i = 0; i < size; i++) {
                    DimletKey key = DimletKey.create(buf);
                    DimletSettings settings = new DimletSettings(buf);
                    dimlets.put(key, settings);
                }
                return new PacketSendDimletPackages(dimlets);
            }
    );

    public PacketSendDimletPackages(Map<DimletKey, DimletSettings> dimlets) {
        this.dimlets = new HashMap<>(dimlets);
    }

    public static PacketSendDimletPackages create(Map<DimletKey, DimletSettings> collected) {
        return new PacketSendDimletPackages(collected);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            RegistryAccess access = SafeClientTools.getClientWorld().registryAccess();
            DimletDictionary dictionary = DimletDictionary.get();
            for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
                dictionary.register(access, entry.getKey(), entry.getValue());
            }
        });
    }
}
