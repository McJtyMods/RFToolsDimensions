package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ByteBufTools {

    public static void writeMapAsStrings(ByteBuf buf, Map<String, String> s) {
        if (s == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(s.size());
            s.entrySet().stream().forEach(entry -> {
                NetworkTools.writeString(buf, entry.getKey());
                NetworkTools.writeString(buf, entry.getValue());
            });
        }
    }

    public static Map<String, String> readMapFromStrings(ByteBuf buf) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        } else {
            Map<String, String> result = new HashMap<>(size);
            for (int i = 0 ; i < size ; i++) {
                String key = NetworkTools.readString(buf);
                String value = NetworkTools.readString(buf);
                result.put(key, value);
            }
            return result;
        }
    }

    public static <T> void writeSetAsStrings(ByteBuf buf, Set<T> s) {
        if (s == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(s.size());
            s.stream().forEach(p -> NetworkTools.writeString(buf, p.toString()));
        }
    }

    public static Set<String> readSetFromStrings(ByteBuf buf) {
        return readSetFromStringsWithMapper(buf, Function.identity());
    }

    public static <T> Set<T> readSetFromStringsWithMapper(ByteBuf buf, Function<String, T> mapper) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        } else {
            Set<T> result = new HashSet<>(size);
            for (int i = 0 ; i < size ; i++) {
                result.add(mapper.apply(NetworkTools.readString(buf)));
            }
            return result;
        }
    }

    public static <T extends Enum<T>> void writeSetAsEnums(ByteBuf buf, Set<T> s) {
        if (s == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(s.size());
            s.stream().forEach(p -> buf.writeShort(p.ordinal()));
        }
    }

    public static <T> Set<T> readSetFromShortsWithMapper(ByteBuf buf, Function<Integer, T> mapper) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        } else {
            Set<T> result = new HashSet<>(size);
            for (int i = 0 ; i < size ; i++) {
                result.add(mapper.apply((int) buf.readShort()));
            }
            return result;
        }
    }

    public static void writeSetAsShorts(ByteBuf buf, Set<Integer> s) {
        if (s == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(s.size());
            s.stream().forEach(buf::writeShort);
        }
    }

    public static Set<Integer> readSetFromShorts(ByteBuf buf) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        } else {
            Set<Integer> result = new HashSet<>(size);
            for (int i = 0 ; i < size ; i++) {
                result.add((int) buf.readShort());
            }
            return result;
        }
    }
}
