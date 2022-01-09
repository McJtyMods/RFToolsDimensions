package mcjty.rftoolsdim.dimension.power;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;

public class DamageSourcePowerLow extends DamageSource {
    public DamageSourcePowerLow(String damageType) {
        super(damageType);
        bypassArmor();
        bypassMagic();
    }

    @Override
    @Nonnull
    public Component getLocalizedDeathMessage(LivingEntity entity) {
        String s = "death.dimension.powerfailure";
        return new TranslatableComponent(s, entity.getName());
    }
}
