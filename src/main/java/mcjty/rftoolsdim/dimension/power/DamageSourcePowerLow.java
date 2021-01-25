package mcjty.rftoolsdim.dimension.power;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DamageSourcePowerLow extends DamageSource {
    public DamageSourcePowerLow(String damageType) {
        super(damageType);
        setDamageBypassesArmor();
        setDamageIsAbsolute();
    }

    @Override
    public ITextComponent getDeathMessage(LivingEntity entity) {
        String s = "death.dimension.powerfailure";
        return new TranslationTextComponent(s, entity.getName());
    }
}
