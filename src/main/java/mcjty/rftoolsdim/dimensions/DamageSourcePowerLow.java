package mcjty.rftoolsdim.dimensions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class DamageSourcePowerLow extends DamageSource {
    public DamageSourcePowerLow(String damageType) {
        super(damageType);
        setDamageBypassesArmor();
        setDamageIsAbsolute();
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity) {
        String s = "death.dimension.powerfailure";
        return new TextComponentTranslation(s, entity.getName());
    }
}
