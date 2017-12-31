package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class PatreonDimletType implements IDimletType {

    @Override
    public String getName() {
        return "Patreon";
    }

    @Override
    public String getOpcode() {
        return "P";
    }

    @Override
    public String getTextureName() {
        return "patreonDimlet";
    }

    @Override
    public void setupFromConfig(Configuration cfg) {
    }

    @Override
    public boolean isModifier() {
        return false;
    }

    @Override
    public boolean isModifiedBy(DimletType type) {
        return false;
    }

    @Override
    public float getModifierCreateCostFactor(DimletType modifierType, DimletKey key) {
        return 1.0f;
    }

    @Override
    public float getModifierMaintainCostFactor(DimletType modifierType, DimletKey key) {
        return 1.0f;
    }

    @Override
    public float getModifierTickCostFactor(DimletType modifierType, DimletKey key) {
        return 1.0f;
    }

    @Override
    public boolean isInjectable(DimletKey key) {
        return true;
    }

    @Override
    public void inject(DimletKey key, DimensionInformation dimensionInformation) {
        if ("McJty".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_FIREWORKS);
        } else if ("SickHippie".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_SICKMOON);
            dimensionInformation.setPatreonBit(Patreons.PATREON_SICKSUN);
        } else if ("Nissenfeld".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_RABBITMOON);
            dimensionInformation.setPatreonBit(Patreons.PATREON_RABBITSUN);
        } else if ("Lockesly".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_PINKPILLARS);
        } else if ("Puppeteer".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_PUPPETEER);
        } else if ("Rouven".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_LAYEREDMETA);
        } else if ("FireBall".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_COLOREDPRISMS);
        } else if ("DarkCorvuz".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_DARKCORVUS);
        } else if ("TomWolf".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_TOMWOLF);
        } else if ("Kenney".equals(key.getId())) {
            dimensionInformation.setPatreonBit(Patreons.PATREON_KENNEY);
        }
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        dimlets = DimensionInformation.extractType(DimletType.DIMLET_PATREON, dimlets);
        if (dimlets.isEmpty()) {
            return;
        }

        for (Pair<DimletKey, List<DimletKey>> dimlet : dimlets) {
            inject(dimlet.getKey(), dimensionInformation);
        }
    }

    @Override
    public String[] getInformation() {
        return new String[] { "Patreon dimlets are in honor of a player and add purely cosmetic features to dimensions" };
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        return null;
    }
}
