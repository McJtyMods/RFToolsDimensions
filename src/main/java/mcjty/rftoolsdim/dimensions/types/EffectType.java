package mcjty.rftoolsdim.dimensions.types;

import java.util.HashMap;
import java.util.Map;

public enum EffectType {
    EFFECT_NONE("None"),
    EFFECT_NOGRAVITY("NoGravity"),
    EFFECT_POISON("Poison1"),
    EFFECT_POISON2("Poison2"),
    EFFECT_POISON3("Poison3"),
    EFFECT_MOVESLOWDOWN("MoveSlowDown1"),
    EFFECT_MOVESLOWDOWN2("MoveSlowDown2"),
    EFFECT_MOVESLOWDOWN3("MoveSlowDown3"),
    EFFECT_MOVESLOWDOWN4("MoveSlowDown4"),
    EFFECT_MOVESPEED("MoveSpeed1"),
    EFFECT_MOVESPEED2("MoveSpeed2"),
    EFFECT_MOVESPEED3("MoveSpeed3"),
    EFFECT_DIGSLOWDOWN("DigSlowDown1"),
    EFFECT_DIGSLOWDOWN2("DigSlowDown2"),
    EFFECT_DIGSLOWDOWN3("DigSlowDown3"),
    EFFECT_DIGSLOWDOWN4("DigSlowDown4"),
    EFFECT_DIGSPEED("DigSpeed1"),
    EFFECT_DIGSPEED2("DigSpeed2"),
    EFFECT_DIGSPEED3("DigSpeed3"),
    EFFECT_DAMAGEBOOST("DamageBoost1"),
    EFFECT_DAMAGEBOOST2("DamageBoost2"),
    EFFECT_DAMAGEBOOST3("DamageBoost3"),
    EFFECT_INSTANTHEALTH("InstantHealth"),
    EFFECT_HARM("Harm"),
    EFFECT_JUMP("Jump1"),
    EFFECT_JUMP2("Jump2"),
    EFFECT_JUMP3("Jump3"),
    EFFECT_REGENERATION("Regeneration1"),
    EFFECT_REGENERATION2("Regeneration2"),
    EFFECT_REGENERATION3("Regeneration3"),
    EFFECT_CONFUSION("Confusion"),
    EFFECT_RESISTANCE("Resistance1"),
    EFFECT_RESISTANCE2("Resistance2"),
    EFFECT_RESISTANCE3("Resistance3"),
    EFFECT_FIRERESISTANCE("FireResistance"),
    EFFECT_WATERBREATHING("WaterBreathing"),
    EFFECT_INVISIBILITY("Invisibility"),
    EFFECT_BLINDNESS("Blindness"),
    EFFECT_NIGHTVISION("NightVision"),
    EFFECT_HUNGER("Hunger1"),
    EFFECT_HUNGER2("Hunger2"),
    EFFECT_HUNGER3("Hunger3"),
    EFFECT_WEAKNESS("Weakness1"),
    EFFECT_WEAKNESS2("Weakness2"),
    EFFECT_WEAKNESS3("Weakness3"),
    EFFECT_WITHER("Wither1"),
    EFFECT_WITHER2("Wither2"),
    EFFECT_WITHER3("Wither3"),
    EFFECT_HEALTHBOOST("HealthBoost1"),
    EFFECT_HEALTHBOOST2("HealthBoost2"),
    EFFECT_HEALTHBOOST3("HealthBoost3"),
    EFFECT_ABSORPTION("Absorption1"),
    EFFECT_ABSORPTION2("Absorption2"),
    EFFECT_ABSORPTION3("Absorption3"),
    EFFECT_SATURATION("Saturation1"),
    EFFECT_SATURATION2("Saturation2"),
    EFFECT_SATURATION3("Saturation3"),
    EFFECT_FLIGHT("Flight"),
    EFFECT_STRONGMOBS("StrongMobs"),
    EFFECT_BRUTALMOBS("BrutalMobs");

    private static final Map<String,EffectType> EFFECT_TYPE_MAP = new HashMap<>();

    static {
        for (EffectType type : EffectType.values()) {
            EFFECT_TYPE_MAP.put(type.getId(), type);
        }
    }

    private final String id;

    EffectType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static EffectType getEffectById(String id) {
        return EFFECT_TYPE_MAP.get(id);
    }

}
