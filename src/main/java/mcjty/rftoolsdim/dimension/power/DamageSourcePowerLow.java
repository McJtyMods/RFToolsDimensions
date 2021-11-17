package mcjty.rftoolsdim.dimension.power;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class DamageSourcePowerLow extends DamageSource {
    public DamageSourcePowerLow(String damageType) {
        super(damageType);
        bypassArmor();
        bypassMagic();
    }

    @Override
    @Nonnull
    public ITextComponent getLocalizedDeathMessage(LivingEntity entity) {
        String s = "death.dimension.powerfailure";
        return new TranslationTextComponent(s, entity.getName());
    }
}
