package mcjty.rftoolsdim.dimensions.world;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;

import javax.annotation.Nullable;

public class UpsidedownWorld extends WorldServer {

    public WorldServer worldObj;

    public UpsidedownWorld(WorldServer worldServer) {
        super(worldServer.getMinecraftServer(), worldServer.getSaveHandler(), worldServer.getWorldInfo(), worldServer.provider.getDimension(), worldServer.profiler);
        this.worldObj = worldServer;
    }

    @Override
    public int getHeight(int x, int z) {
        // This needs to be done differently
        Chunk chunk = worldObj.getChunkFromChunkCoords(x >> 4, z >> 4);
        int y = 0;
        for (y = 0; y < 128; y++) {
            IBlockState blockState = chunk.getBlockState(x & 15, y, z & 15);
            if (blockState.getBlock() != Blocks.AIR) {
                return 127 - y;
            }
        }

        return worldObj.getHeight(x, z);
    }

    @Nullable
    @Override
    public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataID) {
        return worldObj.loadData(clazz, dataID);
    }

    @Override
    public void setData(String dataID, WorldSavedData worldSavedDataIn) {
        worldObj.setData(dataID, worldSavedDataIn);
    }

    @Nullable
    @Override
    public MapStorage getMapStorage() {
        return worldObj.getMapStorage();
    }

    @Override
    public int getUniqueDataId(String key) {
        return worldObj.getUniqueDataId(key);
    }

    @Override
    public void saveAllChunks(boolean all, @Nullable IProgressUpdate progressCallback) throws MinecraftException {
        worldObj.saveAllChunks(all, progressCallback);
    }

    @Override
    public void tick() {
        worldObj.tick();
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return worldObj.getBiome(pos);
    }

    @Override
    public Biome getBiomeForCoordsBody(BlockPos pos) {
        return worldObj.getBiomeForCoordsBody(pos);
    }

    @Override
    public Chunk getChunkFromBlockCoords(BlockPos pos) {
        return worldObj.getChunkFromBlockCoords(pos);
    }

    @Override
    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
        return worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
    }

    @Override
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
        BlockPos p = worldObj.getTopSolidOrLiquidBlock(pos);
//                return new BlockPos(p.getX(), 255-p.getY(), p.getZ());
        return new BlockPos(p.getX(), getHeight(p.getX(), p.getZ()), p.getZ());
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return worldObj.getChunkProvider().chunkExists(x, z);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (newState.getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (newState.getBlock() instanceof BlockFalling) {
            return true;
        }
        if (pos.getY() >= 128) {
            return worldObj.setBlockState(pos, newState, flags);
        } else {
            return worldObj.setBlockState(new BlockPos(pos.getX(), 127 - pos.getY(), pos.getZ()), newState, flags);
        }
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (pos.getY() >= 128) {
            return worldObj.getBlockState(pos);
        } else {
            return worldObj.getBlockState(new BlockPos(pos.getX(), 127 - pos.getY(), pos.getZ()));
        }
    }
}
