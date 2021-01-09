package mcjty.rftoolsdim.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Consumer;

public class DimensionUpdatePacket implements Consumer<NetworkEvent.Context> {

    public static final DimensionUpdatePacket INVALID = new DimensionUpdatePacket(null, false);

    public static final Codec<DimensionUpdatePacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            World.CODEC.optionalFieldOf("id", null).forGetter(DimensionUpdatePacket::getId),
            Codec.BOOL.fieldOf("add").forGetter(DimensionUpdatePacket::getAdd)
    ).apply(instance, DimensionUpdatePacket::new));

    private final RegistryKey<World> id;

    public RegistryKey<World> getId() {
        return this.id;
    }

    private final boolean add;

    public boolean getAdd() {
        return this.add;
    }

    public DimensionUpdatePacket(RegistryKey<World> id, boolean add) {
        this.id = id;
        this.add = add;
    }

    @Override
    public void accept(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandlers.handleDimensionUpdatePacket(this));
    }

}