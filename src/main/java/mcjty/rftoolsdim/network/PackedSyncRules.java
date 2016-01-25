package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.rftoolsdim.config.Filter;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sync dimlet rules from server to client.
 */
public class PackedSyncRules implements IMessage {

    private List<Pair<Filter, Settings>> rules;

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rules.size());
        for (int i = 0 ; i < rules.size() ; i++) {

        }
    }

    public PackedSyncRules() {
    }

    public PackedSyncRules(List<Pair<Filter, Settings>> rules) {
        this.rules = rules;
    }

    public static class Handler implements IMessageHandler<PackedSyncRules, IMessage> {
        @Override
        public IMessage onMessage(PackedSyncRules message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> SyncRulesHelper.syncRulesFromServer(message));
            return null;
        }

    }
}
