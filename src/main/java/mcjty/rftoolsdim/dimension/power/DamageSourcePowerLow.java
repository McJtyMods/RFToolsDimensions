package mcjty.rftoolsdim.dimension.power;

import mcjty.lib.varia.ComponentFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class DamageSourcePowerLow /* @todo 1.19.4 extends DamageSource {
    public DamageSourcePowerLow(String damageType) {
        super(damageType);
        bypassArmor();
        bypassMagic();
    }

    @Override
    @Nonnull
    public Component getLocalizedDeathMessage(LivingEntity entity) {
        String s = "death.dimension.powerfailure";
        return ComponentFactory.translatable(s, entity.getName());
    }
}
*/ {}