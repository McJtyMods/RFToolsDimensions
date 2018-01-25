package mcjty.rftoolsdim.dimensions.world;

import java.lang.reflect.Field;
import java.util.Arrays;

import net.minecraft.world.chunk.ChunkPrimer;

public class ChunkPrimerHelper {
    private static final Field addField;
    static {
        Field af;
        try {
            af = ChunkPrimer.class.getField("add");
        } catch (NoSuchFieldException e) {
            // NotEnoughIDs isn't present
            af = null;
        }
        addField = af;
    }

    private final ChunkPrimer primer;
    private final byte[] add;

    public ChunkPrimerHelper(ChunkPrimer chunkPrimer) {
        primer = chunkPrimer;
        try {
            add = addField == null ? null : (byte[]) addField.get(chunkPrimer);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int getData(int index) {
        int id = primer.data[index];
        if(add != null) id |= add[index] << 16;
        return id;
    }

    public void setData(int index, int id) {
        primer.data[index] = (char) (id & 0xFFFF);
        if(add != null) add[index] = (byte) (id >>> 16);
    }

    // From 's' (inclusive) to 'e' (exclusive)
    public void setDataRange(int s, int e, int id) {
        Arrays.fill(primer.data, s, e, (char) (id & 0xFFFF));
        if(add != null) Arrays.fill(add, s, e, (byte) (id >>> 16));
    }

    public ChunkPrimer getPrimer() {
        return primer;
    }
}
