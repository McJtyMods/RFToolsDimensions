package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.description.SkyDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.types.PatreonType;
import mcjty.rftoolsdim.dimensions.types.SkyType;
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
        switch(key.getId()) {
        case "McJty":
        case "SickHippie":
        case "Nissenfeld":
        case "DarkCorvuz":
        case "TomWolf":
        case "Kenney":
            return true;
        default:
            Logging.getLogger().catching(new RuntimeException("Unknown Patreon dimlet ID " + key.getId()));
            //$FALL-THROUGH$
        case "Lockesly":
        case "Puppeteer":
        case "Rouven":
        case "FireBall":
            return false;
        }
    }

    @Override
    public void inject(DimletKey key, DimensionInformation dimensionInformation) {
        switch(key.getId()) {
        case "McJty":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_FIREWORKS);
            break;
        case "SickHippie":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_SICKMOON);
            dimensionInformation.setPatreonBit(PatreonType.PATREON_SICKSUN);
            break;
        case "Nissenfeld":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_RABBITMOON);
            dimensionInformation.setPatreonBit(PatreonType.PATREON_RABBITSUN);
            break;
        case "Lockesly":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_PINKPILLARS);
            break;
        case "Puppeteer":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_PUPPETEER);
            break;
        case "Rouven":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_LAYEREDMETA);
            break;
        case "FireBall":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_COLOREDPRISMS);
            break;
        case "DarkCorvuz":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_DARKCORVUS);
            SkyDescriptor.Builder builder = new SkyDescriptor.Builder();
            builder.combine(dimensionInformation.getSkyDescriptor());
            builder.skyType(SkyType.SKY_STARS3);
            dimensionInformation.setSkyDescriptor(builder.build());
            break;
        case "TomWolf":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_TOMWOLF);
            break;
        case "Kenney":
            dimensionInformation.setPatreonBit(PatreonType.PATREON_KENNEY);
            break;
        default:
            Logging.getLogger().catching(new RuntimeException("Unknown Patreon dimlet ID " + key.getId()));
        }
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        for (Pair<DimletKey, List<DimletKey>> dimlet : DimensionInformation.extractType(DimletType.DIMLET_PATREON, dimlets)) {
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
