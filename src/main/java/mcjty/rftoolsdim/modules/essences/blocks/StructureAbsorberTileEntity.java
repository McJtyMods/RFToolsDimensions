package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.data.StructureAbsorberData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.component.DataComponentMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static mcjty.lib.builder.TooltipBuilder.*;

public class StructureAbsorberTileEntity extends TickingTileEntity {

    public StructureAbsorberTileEntity(BlockPos pos, BlockState state) {
        super(EssencesModule.TYPE_STRUCTURE_ABSORBER.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .tileEntitySupplier(StructureAbsorberTileEntity::new)
                .topDriver(RFToolsDimensionsTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:dimlets/dimlet_workbench"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(),
                        parameter("block", StructureAbsorberTileEntity::getStructureName),
                        parameter("progress", StructureAbsorberTileEntity::getProgressName)
                )) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    private static String getStructureName(ItemStack stack) {
        StructureAbsorberData data = stack.get(EssencesModule.ITEM_STRUCTURE_ABSORBER_DATA);
        if (data == null || data.structure() == null) {
            return "<Not Set>";
        } else {
            return I18n.get(data.structure().toLanguageKey(Registries.STRUCTURE.location().getPath()).replace('/', '.'));
        }
    }

    public static ResourceLocation getStructure(ItemStack stack) {
        StructureAbsorberData data = stack.getOrDefault(EssencesModule.ITEM_STRUCTURE_ABSORBER_DATA, StructureAbsorberData.DEFAULT);
        return data.structure();
    }

    private static String getProgressName(ItemStack stack) {
        StructureAbsorberData data = stack.get(EssencesModule.ITEM_STRUCTURE_ABSORBER_DATA);
        if (data == null) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxStructureAbsorption.get() - data.absorbing()) * 100) / EssencesConfig.maxStructureAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        StructureAbsorberData data = stack.get(EssencesModule.ITEM_STRUCTURE_ABSORBER_DATA);
        if (data == null) {
            return -1;
        } else {
            return ((EssencesConfig.maxStructureAbsorption.get() - data.absorbing()) * 100) / EssencesConfig.maxStructureAbsorption.get();
        }
    }

    @Override
    protected void tickClient() {
        StructureAbsorberData data = getData(EssencesModule.STRUCTURE_ABSORBER_DATA);
        if (data.absorbing() > 0) {
            RandomSource rand = level.random;

            double u = rand.nextFloat() * 2.0f - 1.0f;
            double v = (float) (rand.nextFloat() * 2.0f * Math.PI);
            double x = Math.sqrt(1 - u * u) * Math.cos(v);
            double y = Math.sqrt(1 - u * u) * Math.sin(v);
            double z = u;
            double r = 1.0f;

            level.addParticle(ParticleTypes.PORTAL, getBlockPos().getX() + 0.5f + x * r, getBlockPos().getY() + 0.5f + y * r, getBlockPos().getZ() + 0.5f + z * r, -x, -y, -z);
        }
    }

    public int getAbsorbing() {
        StructureAbsorberData data = getData(EssencesModule.STRUCTURE_ABSORBER_DATA);
        return data.absorbing();
    }

    public ResourceLocation getAbsorbingStructure() {
        StructureAbsorberData data = getData(EssencesModule.STRUCTURE_ABSORBER_DATA);
        return data.structure();
    }

    @Override
    protected void tickServer() {
        StructureAbsorberData data = getData(EssencesModule.STRUCTURE_ABSORBER_DATA);
        ResourceLocation structureId = data.structure();
        int absorbing = data.absorbing();
        if (structureId == null) {
            ChunkPos cp = new ChunkPos(worldPosition);
            var references = level.getChunk(cp.x, cp.z).getAllReferences();
            List<ResourceLocation> structures = new ArrayList<>();
            for (var entry : references.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    structures.add(Tools.getId(level, entry.getKey()));
                }
            }
            if (!structures.isEmpty()) {
                if (structures.size() == 1) {
                    structureId = structures.get(0);
                } else {
                    structureId = structures.get(level.random.nextInt(structures.size()));
                }
                absorbing = EssencesConfig.maxStructureAbsorption.get();
            }
        }

        if (absorbing > 0) {
            if (!isValidStructure()) {
                return;
            }

            absorbing--;
        }
        setData(EssencesModule.STRUCTURE_ABSORBER_DATA, new StructureAbsorberData(structureId, absorbing));
    }

    private boolean isValidStructure() {
        StructureAbsorberData data = getData(EssencesModule.STRUCTURE_ABSORBER_DATA);
        ChunkPos cp = new ChunkPos(worldPosition);
        var references = level.getChunk(cp.x, cp.z).getAllReferences();
        for (var entry : references.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (Objects.equals(data.structure(), Tools.getId(level, entry.getKey()).toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        StructureAbsorberData data = input.get(EssencesModule.ITEM_STRUCTURE_ABSORBER_DATA);
        if (data != null) {
            setData(EssencesModule.STRUCTURE_ABSORBER_DATA, data);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        StructureAbsorberData data = getData(EssencesModule.STRUCTURE_ABSORBER_DATA);
        builder.set(EssencesModule.ITEM_STRUCTURE_ABSORBER_DATA.get(), data);
    }
}
