package mcjty.rftoolsdim.varia;

public class GenericTools {
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> castClass(Class<?> from, Class<T> to) {
        if(!to.isAssignableFrom(from)) {
            throw new ClassCastException("Cannot cast Class<" + from.getName() + "> to Class<? extends " + to.getName() + ">");
        } else {
            return (Class<? extends T>) from;
        }
    }
}
