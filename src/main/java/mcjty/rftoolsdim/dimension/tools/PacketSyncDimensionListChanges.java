package mcjty.rftoolsdim.dimension.tools;

import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketSyncDimensionListChanges {
    private final Set<ResourceKey<Level>> newDimensions;
    private final Set<ResourceKey<Level>> removedDimensions;

    public PacketSyncDimensionListChanges(final Set<ResourceKey<Level>> newDimensions, final Set<ResourceKey<Level>> removedDimensions) {
        this.newDimensions = newDimensions;
        this.removedDimensions = removedDimensions;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.newDimensions.size());
        for (final ResourceKey<Level> key : this.newDimensions) {
            buf.writeResourceLocation(key.location());
        }

        buf.writeVarInt(this.removedDimensions.size());
        for (final ResourceKey<Level> key : this.removedDimensions) {
            buf.writeResourceLocation(key.location());
        }
    }

    public PacketSyncDimensionListChanges(FriendlyByteBuf buf) {
        newDimensions = new HashSet<>();
        removedDimensions = new HashSet<>();

        final int newDimensionCount = buf.readVarInt();
        for (int i = 0; i < newDimensionCount; i++) {
            final ResourceLocation worldID = buf.readResourceLocation();
            newDimensions.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, worldID));
        }

        final int removedDimensionCount = buf.readVarInt();
        for (int i = 0; i < removedDimensionCount; i++) {
            final ResourceLocation worldID = buf.readResourceLocation();
            removedDimensions.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, worldID));
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextGetter) {
        final NetworkEvent.Context context = contextGetter.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                final Set<ResourceKey<Level>> commandSuggesterLevels = player.connection.levels();
                commandSuggesterLevels.addAll(this.newDimensions);
                for (final ResourceKey<Level> key : this.removedDimensions) {
                    commandSuggesterLevels.remove(key);
                }
            }
        });
        context.setPacketHandled(true);
    }


    /**
     * Notifies clients that their list of dimension IDs needs to be updated.
     * This clientside list is normally only used for the command suggester.
     *
     * @param newDimensions keys to add to clients' dimension lists
     * @param removedDimensions keys to remove from clients' dimension lists
     *
     * @apiNote Internal; this is invoked by {@link DynamicDimensionManager}
     * when that's used to add or remove dynamic dimensions,
     * so mods shouldn't need to call this themselves
     */
    public static void updateClientDimensionLists(Set<ResourceKey<Level>> newDimensions, Set<ResourceKey<Level>> removedDimensions) {
        RFToolsDimMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketSyncDimensionListChanges(newDimensions,removedDimensions));
    }
}
