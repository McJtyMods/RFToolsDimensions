package mcjty.rftoolsdim.tools;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReflectionHelper {

    /**
     * Gets a getter-like object for a reflective field. Only to be used for obfuscatable vanilla minecraft fields
     *
     * @param <FIELDHOLDER>    The type of the object containing the field
     * @param <FIELDTYPE>      The type of the values the field would contain
     * @param fieldHolderClass The class of the object containing the field
     * @param fieldName        The SRG (intermediary-obfuscated) name of the field
     * @return A getter for the field
     */
    public static <FIELDHOLDER, FIELDTYPE> Function<FIELDHOLDER, FIELDTYPE> getInstanceFieldGetter(Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        // forge's ORH is needed to reflect into vanilla minecraft java
        Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
        return getInstanceFieldGetter(field);
    }

    public static <FIELDHOLDER, FIELDTYPE> MutableInstanceField<FIELDHOLDER, FIELDTYPE> getInstanceField(Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        return new MutableInstanceField<FIELDHOLDER, FIELDTYPE>(fieldHolderClass, fieldName);
    }

    public static class MutableInstanceField<FIELDHOLDER, FIELDTYPE> {
        private final Function<FIELDHOLDER, FIELDTYPE> getter;
        private final BiConsumer<FIELDHOLDER, FIELDTYPE> setter;

        private MutableInstanceField(Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
            Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
            this.getter = getInstanceFieldGetter(field);
            this.setter = getInstanceFieldSetter(field);
        }

        /**
         * Returns the current value of the field in a given instance
         *
         * @param instance The object containing the instance field to get the value from
         * @return The value in that field
         */
        public FIELDTYPE get(FIELDHOLDER instance) {
            return this.getter.apply(instance);
        }

        /**
         * Sets an object's field to the given value
         *
         * @param instance The object containing the instance field to set the value in
         * @param value    The value to set
         */
        public void set(FIELDHOLDER instance, FIELDTYPE value) {
            this.setter.accept(instance, value);
        }
    }

    @SuppressWarnings("unchecked")
    // throws ClassCastException if the types are wrong, the returned function can also throw RuntimeException
    private static <FIELDHOLDER, FIELDTYPE> Function<FIELDHOLDER, FIELDTYPE> getInstanceFieldGetter(Field field) {
        return instance -> {
            try {
                return (FIELDTYPE) (field.get(instance));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    // the returned function throws RuntimeException if the types are wrong
    private static <FIELDHOLDER, FIELDTYPE> BiConsumer<FIELDHOLDER, FIELDTYPE> getInstanceFieldSetter(Field field) {
        return (instance, value) -> {
            try {
                field.set(instance, value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

}