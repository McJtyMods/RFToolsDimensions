package mcjty.rftoolsdim.dimension.terraintypes;

public class WavesChunkGeneratorOld { //extends BaseChunkGenerator<WavesChunkGenerator.Config> {

//    @Override
//    protected void makeBaseInternal(IWorld world, int seaLevel, ObjectList<AbstractVillagePiece> villagePieces, ObjectList<JigsawJunction> jigsawJunctions, int chunkX, int chunkZ, int x, int z, List<BlockState> baseBlocks, ChunkPrimer primer, Heightmap heightmapOceanFloor, Heightmap heightmapWorldSurface) {
//        ChunkPos chunkpos = primer.getPos();
//
//        BlockPos.Mutable pos = new BlockPos.Mutable();
//
//        for (x = 0; x < 16; x++) {
//            for (z = 0; z < 16; z++) {
//                int realx = chunkpos.x * 16 + x;
//                int realz = chunkpos.z * 16 + z;
//                int height = (int) (65 + Math.sin(realx / 20.0f)*10 + Math.cos(realz / 20.0f)*10);
//                for (int y = 1 ; y < height ; y++) {
//                    primer.setBlockState(pos.setPos(x, y, z), baseBlocks.get(world.getRandom().nextInt(baseBlocks.size())), false);
//                }
//            }
//        }
//
//    }

}
