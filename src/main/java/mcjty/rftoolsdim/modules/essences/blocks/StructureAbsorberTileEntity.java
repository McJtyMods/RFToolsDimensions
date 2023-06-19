package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class StructureAbsorberTileEntity extends TickingTileEntity {

    public StructureAbsorberTileEntity(BlockPos pos, BlockState state) {
        super(EssencesModule.TYPE_STRUCTURE_ABSORBER.get(), pos, state);
    }

    private int absorbing = 0;
    private String structureId = null;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .tileEntitySupplier(StructureAbsorberTileEntity::new)
                .topDriver(RFToolsDimensionsTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/dimlet_workbench"))
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
        String structure = NBTTools.getInfoNBT(stack, CompoundTag::getString, "structure", null);
        if (structure == null) {
            return "<Not Set>";
        } else {
            ResourceLocation id = new ResourceLocation(structure);
            return id.getPath();
        }
    }

    public static String getStructure(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getString, "structure", null);
    }

    private static String getProgressName(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxStructureAbsorption.get() - absorbing) * 100) / EssencesConfig.maxStructureAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return -1;
        } else {
            return ((EssencesConfig.maxStructureAbsorption.get() - absorbing) * 100) / EssencesConfig.maxStructureAbsorption.get();
        }
    }

    @Override
    protected void tickClient() {
        if (absorbing > 0) {
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
        return absorbing;
    }

    public String getAbsorbingStructure() {
        return structureId;
    }

    @Override
    protected void tickServer() {
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
                    structureId = structures.get(0).toString();
                } else {
                    structureId = structures.get(level.random.nextInt(structures.size())).toString();
                }
                absorbing = EssencesConfig.maxStructureAbsorption.get();
                setChanged();
            }
        }

        if (absorbing > 0) {
            if (!isValidStructure()) {
                return;
            }

            absorbing--;
            setChanged();
        }
    }

    private boolean isValidStructure() {
        ChunkPos cp = new ChunkPos(worldPosition);
        var references = level.getChunk(cp.x, cp.z).getAllReferences();
        for (var entry : references.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (structureId.equals(Tools.getId(level, entry.getKey()).toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        info.putInt("absorbing", absorbing);
        if (structureId != null) {
            info.putString("structure", structureId);
        }
    }

    @Override
    public void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        absorbing = info.getInt("absorbing");
        if (info.contains("structure")) {
            structureId = info.getString("structure");
        } else {
            structureId = null;
        }
    }
}
