package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.Filter;
import mcjty.rftoolsdim.config.Settings;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Sync dimlet rules from server to client.
 */
public class PacketSyncRules implements IMessage {

    private List<Pair<Filter, Settings>> rules;

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        rules = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            Filter filter = new Filter(buf);
            Settings settings = new Settings(buf);
            rules.add(Pair.of(filter, settings));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rules.size());
        for (Pair<Filter, Settings> rule : rules) {
            Filter filter = rule.getLeft();
            Settings settings = rule.getRight();
            filter.toBytes(buf);
            settings.toBytes(buf);
        }

        Logging.log("Rules packet size: " + buf.writerIndex() + " of " + buf.array().length);
    }

    public List<Pair<Filter, Settings>> getRules() {
        return rules;
    }

    public PacketSyncRules() {
    }

    public PacketSyncRules(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketSyncRules(List<Pair<Filter, Settings>> rules) {
        this.rules = rules;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            SyncRulesHelper.syncRulesFromServer(this);
        });
        ctx.setPacketHandled(true);
    }
}
