package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.*;

public class LostCitiesTerrainGenerator extends NormalTerrainGenerator {
    private final byte height;

    public static final ResourceLocation LOOT = new ResourceLocation(RFToolsDim.MODID, "chests/lostcitychest");
    private static final int STREETBORDER = 3;


    public LostCitiesTerrainGenerator() {
        super();
        this.height = 63;
    }

    private static class Level {
        private final String[] floor;

        public Level(String[] floor) {
            this.floor = floor;
        }

        IBlockState get(int x, int y, int z) {
            return getMapping().get(floor[y].charAt(z * 16 + x));
        }

        Character getC(int x, int y, int z) {
            return floor[y].charAt(z * 16 + x);
        }
    }

    private static Level[] FOUNTAINS = new Level[]
            {
                    new Level(new String[]{
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      xxxx      " +
                                    "      xWWx      " +
                                    "      xWWx      " +
                                    "      xxxx      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                "
                    }),
                    new Level(new String[]{
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "       xx       " +
                                    "      xWWx      " +
                                    "     xWWWWx     " +
                                    "     xWWWWx     " +
                                    "      xWWx      " +
                                    "       xx       " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                "
                    }),
                    new Level(new String[]{
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      xxxx      " +
                                    "      xWWx      " +
                                    "      xWWx      " +
                                    "      xxxx      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      :  :      " +
                                    "                " +
                                    "                " +
                                    "      :  :      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      :  :      " +
                                    "                " +
                                    "                " +
                                    "      :  :      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      xxxx      " +
                                    "      x  x      " +
                                    "      x  x      " +
                                    "      xxxx      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                "
                    })
            };

    private static Level[] TOPS = new Level[]
            {
                    new Level(new String[]{
                            "" +
                                    "                " +
                                    "                " +
                                    "            :   " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     :   :      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     :   :      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "            :   " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     :   :      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     :   :      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "            :   " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     xxxxx      " +
                                    "     xxxxx      " +
                                    "     xxxxx      " +
                                    "     xxxxx      " +
                                    "     xxxxx      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "            :   " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "            :   " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                    }),
                    new Level(new String[]{
                            "" +
                                    "                " +
                                    "  xxx           " +
                                    "  x x           " +
                                    "  xxx           " +
                                    "                " +
                                    "                " +
                                    "    =======     " +
                                    "    =:   :=     " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "    =:   :=     " +
                                    "    =======     " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "  : :           " +
                                    "                " +
                                    "  : :           " +
                                    "                " +
                                    "                " +
                                    "    =======     " +
                                    "    =:   :=     " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "    =:   :=     " +
                                    "    =======     " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "  : :           " +
                                    "                " +
                                    "  : :           " +
                                    "                " +
                                    "                " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      :         " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "      :         " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "#GGDDDGGDDDDGDD#" +
                                    "#GDGDGGDGGGDDGG#" +
                                    "#GGGGDDGDDDDGDD#" +
                                    "#DDDDGGGDDDGDDD#" +
                                    "#GGDDDDGDDGDDDD#" +
                                    "#DGDGGGDGGDDDDD#" +
                                    "#DGGGGGGDDDDDDD#" +
                                    "#GDGDDDGGGDDDDD#" +
                                    "#DDGDDDlGDGDDDD#" +
                                    "#GDGDDDDGDDGDDD#" +
                                    "#DDGDDDGDGDDGDD#" +
                                    "#DGGDGGDDDGDDDD#" +
                                    "#DGDDDDDGDGDDGD#" +
                                    "#GGDDDGDDDDGGDD#" +
                                    "################",
                            "" +
                                    "w              w" +
                                    "      p      p  " +
                                    "    p   xxxx    " +
                                    "        xWWx    " +
                                    "      p xWWx    " +
                                    "  p     xxxx p  " +
                                    "      p  p      " +
                                    "    p           " +
                                    "             p  " +
                                    "                " +
                                    "  p       p     " +
                                    "                " +
                                    "    p  p        " +
                                    "            p   " +
                                    "     p          " +
                                    "w              w",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                    }),
                    new Level(new String[]{
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     QQ QQ      " +
                                    "     Q   Q      " +
                                    "                " +
                                    "     Q   Q      " +
                                    "     QQ QQ      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "     QQ QQ      " +
                                    "     Q   Q      " +
                                    "                " +
                                    "     Q   Q      " +
                                    "     QQ QQ      " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "    xxxxxxx     " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                            "" +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                " +
                                    "                ",
                    })
            };


    private static Level[] FLOORS = new Level[]
            {
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=  x        x  =" +
                                    "=  xF       x  =" +
                                    "@  xF      Lx  @" +
                                    "=  xD      Lx  =" +
                                    "=  x        x  =" +
                                    "=              =" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "=  x   l    x  =" +
                                    "=  xxxxxxxxxx  =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=     F        =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "@  x       Lx  @" +
                                    "=  x*      Lx  =" +
                                    "=  x        x  =" +
                                    "=              =" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "=  x   l    x  =" +
                                    "=  xxxxxxxxxx  =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "@  x        x  @" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "=  x        x  =" +
                                    "=  x   l    x  =" +
                                    "=  xxxxxxxxxx  =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "####2###########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=  F      QQQ  =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=             F=" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=      l       =" +
                                    "=xxxxxxxxxxx  x=" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=         **   =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=      l       =" +
                                    "=x::xxxxxxxx  x=" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=              =" +
                                    "=      l       =" +
                                    "=xxxxxxxxxxxxxx=" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "@WWWWWWWWWWWWWW@" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "================" +
                                    "=              =" +
                                    "=              =" +
                                    "=L             =" +
                                    "=F     l       =" +
                                    "=F    xxx::::::=" +
                                    "=      :       =" +
                                    "@      :       @" +
                                    "=      :       =" +
                                    "=      :       =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "@WWWWWWWWWWWWWW@" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "================" +
                                    "=              =" +
                                    "=              =" +
                                    "=L             =" +
                                    "=      l       =" +
                                    "=     xxx::::::=" +
                                    "=      :       =" +
                                    "@      :       @" +
                                    "=      :       =" +
                                    "=      :       =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "@WWWWWWWWWWWWWW@" +
                                    "=WWWWWWWWWWWWWW=" +
                                    "================" +
                                    "=              =" +
                                    "=              =" +
                                    "=L             =" +
                                    "=      l       =" +
                                    "=     xxx::::::=" +
                                    "=      :       =" +
                                    "@      :       @" +
                                    "=      :       =" +
                                    "=      :       =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xx xx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=x xxxxxxxxxxxx=" +
                                    "=     FFF      =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xx xx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=x xxxxxxxx===x=" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=xxxxxxxxxxxxxx=" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "###1############" +
                                    "################" +
                                    "############2###" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "####X1XlX2X#####" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=   LL         =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xx xx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    X l X     =" +
                                    "=  QQxxxxxQQ   =" +
                                    "=      CF      =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=   LL         =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xx xx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=  QQxxxxxQQ   =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=  QQxxxxxQQ   =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xx xx     =" +
                                    "=    x   xxxxxx=" +
                                    "=              =" +
                                    "=    x l x     =" +
                                    "=    xxxxx     =" +
                                    "=      Fx      =" +
                                    "@       x      @" +
                                    "=              =" +
                                    "=       x      =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xx xx     =" +
                                    "=    x   xxx:xx=" +
                                    "=              =" +
                                    "=    x l x     =" +
                                    "=    xxxxx     =" +
                                    "=       x      =" +
                                    "@       x      @" +
                                    "=              =" +
                                    "=       x      =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    xxxxx     =" +
                                    "=    x   xxxxxx=" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=    xxxxx     =" +
                                    "=       x      =" +
                                    "@       x      @" +
                                    "=       x      =" +
                                    "=       x      =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "@              @" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   xL    =" +
                                    "=      l xL    =" +
                                    "=xxxxxxxxx     =" +
                                    "=    F         =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "@              @" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   xL    =" +
                                    "=      l xL    =" +
                                    "=xxxxxxxxx     =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "@      x       @" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=xxxxxxxxx     =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    QQQQQ:::::=" +
                                    "=    Q   Q     =" +
                                    "=    Q   Q     =" +
                                    "=      l       =" +
                                    "=    QQQQQ     =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    QQQQQ:::::=" +
                                    "=    Q   Q     =" +
                                    "=    Q   Q     =" +
                                    "=      l       =" +
                                    "=    QQQQQ     =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=              =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "=    QQQQQ:::::=" +
                                    "=    Q   Q     =" +
                                    "=    Q   Q     =" +
                                    "=    Q l Q     =" +
                                    "=    QQQQQ     =" +
                                    "=              =" +
                                    "@              @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    }),
                    new Level(new String[]{
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "####X1X#X2X#####" +
                                    "#####X###X######" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "#==@========@==#" +
                                    "=      x       =" +
                                    "=      xF      =" +
                                    "@              @" +
                                    "=      x       =" +
                                    "=     Lx       =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   xF    =" +
                                    "=      l x     =" +
                                    "=xxxxXxxxX     =" +
                                    "=       :      =" +
                                    "@       :      @" +
                                    "=   C          =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "@              @" +
                                    "=      x       =" +
                                    "=     Lx       =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=      l x     =" +
                                    "=xxxxxxxxx     =" +
                                    "=       :      =" +
                                    "@       :      @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "#==@========@==#" +
                                    "=      x       =" +
                                    "=      x       =" +
                                    "@      x       @" +
                                    "=      x       =" +
                                    "=     Lx       =" +
                                    "=    xxxxx     =" +
                                    "=    x   x     =" +
                                    "=    x   x     =" +
                                    "=    x l x     =" +
                                    "=xxxxxxxxx     =" +
                                    "=       :      =" +
                                    "@       :      @" +
                                    "=              =" +
                                    "=              =" +
                                    "#==@========@==#",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################",
                            "" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "#######l########" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################" +
                                    "################"
                    })
            };

    public static class GenInfo {
        private final List<BlockPos> spawnerType1 = new ArrayList<>();
        private final List<BlockPos> spawnerType2 = new ArrayList<>();
        private final List<BlockPos> chest = new ArrayList<>();
        private final List<BlockPos> randomFeatures = new ArrayList<>();

        public void addSpawnerType1(BlockPos p) {
            spawnerType1.add(p);
        }

        public void addSpawnerType2(BlockPos p) {
            spawnerType2.add(p);
        }

        public void addChest(BlockPos p) {
            chest.add(p);
        }

        public void addRandomFeatures(BlockPos p) {
            randomFeatures.add(p);
        }

        public List<BlockPos> getSpawnerType1() {
            return spawnerType1;
        }

        public List<BlockPos> getSpawnerType2() {
            return spawnerType2;
        }

        public List<BlockPos> getChest() {
            return chest;
        }

        public List<BlockPos> getRandomFeatures() {
            return randomFeatures;
        }
    }

    private static Map<Character, IBlockState> mapping = null;
    private static List<GenInfo> genInfos = null;

    private static Map<Character, IBlockState> getMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
            mapping.put('#', Blocks.STONEBRICK.getDefaultState());
            mapping.put('=', Blocks.GLASS.getDefaultState());
            mapping.put('@', Blocks.GRAVEL.getDefaultState());      // Will be replaced with glass
            mapping.put(' ', Blocks.AIR.getDefaultState());
            mapping.put('x', Blocks.STONEBRICK.getDefaultState());
            mapping.put('l', Blocks.LADDER.getDefaultState());
            mapping.put('1', Blocks.PLANKS.getDefaultState());      // Monster spawner 1
            mapping.put('2', Blocks.PLANKS.getDefaultState());      // Monster spawner 2
            mapping.put('C', Blocks.PLANKS.getDefaultState());      // Chest
            mapping.put('F', Blocks.PLANKS.getDefaultState());      // Random feature
            mapping.put(':', Blocks.IRON_BARS.getDefaultState());
            mapping.put('D', Blocks.DIRT.getDefaultState());
            mapping.put('G', Blocks.GRASS.getDefaultState());
            mapping.put('p', Blocks.SAPLING.getDefaultState());
            mapping.put('*', Blocks.FLOWER_POT.getDefaultState());
            mapping.put('X', Blocks.MONSTER_EGG.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONEBRICK));
            mapping.put('Q', Blocks.QUARTZ_BLOCK.getDefaultState());
            mapping.put('L', Blocks.BOOKSHELF.getDefaultState());
            mapping.put('W', Blocks.WATER.getDefaultState());
            mapping.put('w', Blocks.COBBLESTONE_WALL.getDefaultState());
        }
        return mapping;
    }

    public static List<GenInfo> getGenInfos() {
        if (genInfos == null) {
            genInfos = new ArrayList<>();
            for (int i = 0; i < FLOORS.length; i++) {
                GenInfo gi = new GenInfo();
                Level level = FLOORS[i];
                for (int y = 0; y < 6; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            Character c = level.getC(x, y, z);
                            ;
                            if (c == '1') {
                                gi.addSpawnerType1(new BlockPos(x, y, z));
                            } else if (c == '2') {
                                gi.addSpawnerType2(new BlockPos(x, y, z));
                            } else if (c == 'C') {
                                gi.addChest(new BlockPos(x, y, z));
                            } else if (c == 'F') {
                                gi.addRandomFeatures(new BlockPos(x, y, z));
                            }
                        }
                    }
                }
                genInfos.add(gi);
            }
        }
        return genInfos;
    }

    public static class BuildingInfo {
        public final boolean hasBuilding;
        public final int fountainType;
        public final int floors;
        public final int floorsBelowGround;
        public final int[] floorTypes;
        public final boolean[] connectionAtX;
        public final boolean[] connectionAtZ;
        public final int topType;
        public final int glassType;
        public final int glassColor;
        public final int buildingStyle;

        public BuildingInfo(int chunkX, int chunkZ, long seed) {
            Random rand = getBuildingRandom(chunkX, chunkZ, seed);
            hasBuilding = (chunkX != 0 || chunkZ != 0) && rand.nextFloat() < .3f;
            if (rand.nextFloat() < .05f) {
                fountainType = rand.nextInt(FOUNTAINS.length);
            } else {
                fountainType = -1;
            }
            floors = rand.nextInt(7);
            floorsBelowGround = rand.nextInt(4);
            floorTypes = new int[floors+floorsBelowGround + 2];
            connectionAtX = new boolean[floors+floorsBelowGround + 2];
            connectionAtZ = new boolean[floors+floorsBelowGround + 2];
            for (int i = 0; i <= floors+floorsBelowGround + 1; i++) {
                floorTypes[i] = rand.nextInt(FLOORS.length);
                connectionAtX[i] = rand.nextFloat() < .6f;
                connectionAtZ[i] = rand.nextFloat() < .6f;
            }
            topType = rand.nextInt(TOPS.length);
            glassType = rand.nextInt(4);
            glassColor = rand.nextInt(5);
            buildingStyle = rand.nextInt(4);
        }

        public boolean hasConnectionAtX(int level) {
            if (level >= connectionAtX.length) {
                return false;
            }
            return connectionAtX[level];
        }

        public boolean hasConnectionAtZ(int level) {
            if (level >= connectionAtZ.length) {
                return false;
            }
            return connectionAtZ[level];
        }
    }

    public static Random getBuildingRandom(int chunkX, int chunkZ, long seed) {
        Random rand = new Random(seed + chunkZ * 341873128712L + chunkX * 132897987541L);
        rand.nextFloat();
        rand.nextFloat();
        return rand;
    }

    private IBlockState street;
    private IBlockState air;
    private IBlockState glass;
    private IBlockState quartz;
    private IBlockState bricks;
    private IBlockState bricks_cracked;

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState baseBlock = provider.dimensionInformation.getBaseBlockForTerrain();
        IBlockState baseLiquid = provider.dimensionInformation.getFluidForTerrain().getDefaultState();

        BuildingInfo info = new BuildingInfo(chunkX, chunkZ, provider.seed);
        setStyle(info);

        DamageArea damageArea = new DamageArea(provider.seed, chunkX, chunkZ);

        byte groundLevel = height;
        byte waterLevel = (byte) (groundLevel-8);

        int buildingtop = 0;
        boolean building = info.hasBuilding;
        if (building) {
            buildingtop = 69 + info.floors * 6;
        }

        Random rand = new Random(provider.seed * 377 + chunkZ * 341873128712L + chunkX * 132897987541L);
        rand.nextFloat();
        rand.nextFloat();

        int cx = chunkX * 16;
        int cz = chunkZ * 16;
        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {

                int height = 0;
                while (height < WorldgenConfiguration.bedrockLayer) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.BEDROCK.getDefaultState());
                    height++;
                }

                while (height < WorldgenConfiguration.bedrockLayer + 30 + rand.nextInt(3)) {
                    BaseTerrainGenerator.setBlockState(primer, index++, baseBlock);
                    height++;
                }

                if (building) {
                    int belowGround = info.floorsBelowGround;

                    while (height < groundLevel - belowGround*6) {
                        BaseTerrainGenerator.setBlockState(primer, index++, height < waterLevel ? baseLiquid : damageArea.damageBlock(baseBlock, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air));
                        height++;
                    }
                    while (height < buildingtop) {
                        IBlockState b = getBlockForLevel(rand, chunkX, chunkZ, info, x, z, height);
                        b = damageArea.damageBlock(b, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air);
                        BaseTerrainGenerator.setBlockState(primer, index++, b);
                        height++;
                    }
                    while (height < buildingtop + 6) {
                        int f = getFloor(height);
                        int floortype = info.topType;
                        Level level = TOPS[floortype];
                        IBlockState b = level.get(x, f, z);
                        b = getReplacementBlock(rand, info, b, false);
                        b = damageArea.damageBlock(b, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air);
                        BaseTerrainGenerator.setBlockState(primer, index++, b);
                        height++;
                    }
                } else {
                    while (height < groundLevel) {
                        BaseTerrainGenerator.setBlockState(primer, index++, height < waterLevel ? baseLiquid : damageArea.damageBlock(baseBlock, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air));
                        height++;
                    }

                    if (isBorder(x, z)) {
                        IBlockState b = baseBlock;
                        if (x <= STREETBORDER && z > STREETBORDER && z < (15 - STREETBORDER) && !new BuildingInfo(chunkX - 1, chunkZ, provider.seed).hasBuilding) {
                            b = street;
                        } else if (x >= (15 - STREETBORDER) && z > STREETBORDER && z < (15 - STREETBORDER) && !new BuildingInfo(chunkX + 1, chunkZ, provider.seed).hasBuilding) {
                            b = street;
                        } else if (z <= STREETBORDER && x > STREETBORDER && x < (15 - STREETBORDER) && !new BuildingInfo(chunkX, chunkZ - 1, provider.seed).hasBuilding) {
                            b = street;
                        } else if (z >= (15 - STREETBORDER) && x > STREETBORDER && x < (15 - STREETBORDER) && !new BuildingInfo(chunkX, chunkZ + 1, provider.seed).hasBuilding) {
                            b = street;
                        }
                        BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(b, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air));
                        height++;
                    } else {
                        BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(street, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air));
                        height++;
                    }

                    if (info.fountainType >= 0) {
                        int l = 0;
                        Level level = FOUNTAINS[info.fountainType];
                        while (l < level.floor.length) {
                            IBlockState b = level.get(x, l, z);
                            b = damageArea.damageBlock(b, rand, damageArea.getDamage(cx + x, height, cz + z), index, bricks, bricks_cracked, quartz, air);
                            BaseTerrainGenerator.setBlockState(primer, index++, b);
                            height++;
                            l++;
                        }
                    }
                }

                int blocks = 256 - height;
                BaseTerrainGenerator.setBlockStateRange(primer, index, index + blocks, air);
                index += blocks;
            }
        }

        if (building) {
            char a = (char) Block.BLOCK_STATE_IDS.get(air);
            char b1 = (char) Block.BLOCK_STATE_IDS.get(bricks);
            char b2 = (char) Block.BLOCK_STATE_IDS.get(bricks_cracked);
            char iron = (char) Block.BLOCK_STATE_IDS.get(Blocks.IRON_BARS.getDefaultState());
            for (int i = 0 ; i < 2 ; i++) {
                index = 0;
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        int belowGround = info.floorsBelowGround;
                        int height = groundLevel - belowGround * 6;
                        index += height;
                        while (height < buildingtop + 6) {
                            if (primer.data[index] != a) {
                                if (primer.data[index + 1] == a
                                        && primer.data[index - 1] == a
                                        && (z == 0 || primer.data[index - 256] == a)
                                        && (z == 15 || primer.data[index + 256] == a)
                                        && (x == 0 || primer.data[index - 256 * 16] == a)
                                        && (x == 15 || primer.data[index + 256 * 16] == a)
                                        ) {
                                    primer.data[index] = a;
                                } else if (primer.data[index - 1] == a && damageArea.damaged[index - 1]) {
                                    if (primer.data[index - 1] == b1 || primer.data[index - 1] == b2) {
                                        primer.data[index - 1] = iron;
                                    } else {
                                        primer.data[index] = a;
                                    }
                                }
                            }
                            index++;
                            height++;
                        }
                        int blocks = 256 - height;
                        index += blocks;
                    }
                }
            }
        }
    }

    private void setStyle(BuildingInfo info) {
        street = Blocks.DOUBLE_STONE_SLAB.getDefaultState();
        air = Blocks.AIR.getDefaultState();

        switch (info.glassColor) {
            case 0: glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.WHITE); break;
            case 1: glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.GRAY); break;
            case 2: glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.LIGHT_BLUE); break;
            case 3: glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLUE); break;
            default: glass = Blocks.GLASS.getDefaultState(); break;
        }

        quartz = Blocks.QUARTZ_BLOCK.getDefaultState();

        switch (info.buildingStyle) {
            case 0:
                bricks = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
                bricks_cracked = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
                break;
            case 1:
                bricks = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
                bricks_cracked = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
                break;
            case 2:
                bricks = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                bricks_cracked = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                break;
            default:
                bricks = Blocks.STONEBRICK.getDefaultState();
                bricks_cracked = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
                break;
        }
    }

    private IBlockState getBlockForLevel(Random rand, int chunkX, int chunkZ, BuildingInfo info, int x, int z, int height) {
        int f = getFloor(height);
        int l = getLevel(height) + info.floorsBelowGround;
        Level level = FLOORS[info.floorTypes[l]];
        IBlockState b = level.get(x, f, z);
        if (x == 0 && z == 8 && f >= 1 && f <= 2 && info.hasConnectionAtX(l)) {
            BuildingInfo info2 = new BuildingInfo(chunkX - 1, chunkZ, provider.seed);
            if (info2.hasBuilding && l <= info2.floors + 1) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        } else if (x == 15 && z == 8 && f >= 1 && f <= 2) {
            BuildingInfo info2 = new BuildingInfo(chunkX + 1, chunkZ, provider.seed);
            if (info2.hasBuilding && l <= info2.floors + 1 && info2.hasConnectionAtX(l)) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        }
        if (z == 0 && x == 8 && f >= 1 && f <= 2 && info.hasConnectionAtZ(l)) {
            BuildingInfo info2 = new BuildingInfo(chunkX, chunkZ - 1, provider.seed);
            if (info2.hasBuilding && l <= info2.floors + 1) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        } else if (z == 15 && x == 8 && f >= 1 && f <= 2) {
            BuildingInfo info2 = new BuildingInfo(chunkX, chunkZ + 1, provider.seed);
            if (info2.hasBuilding && l <= info2.floors + 1 && info2.hasConnectionAtZ(l)) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        }
        boolean down = f == 0 && l == 0;

        return getReplacementBlock(rand, info, b, down);
    }

    private IBlockState getReplacementBlock(Random rand, BuildingInfo info, IBlockState b, boolean down) {
        if (b.getBlock() == Blocks.GRAVEL) {
            switch (info.glassType) {
                case 0: b = glass; break;
                case 1: b = street; break;
                case 2: b = bricks; break;
                case 3: b = quartz; break;
            }
        } else if (b.getBlock() == Blocks.LADDER && down) {
            b = bricks;
        } else if (b.getBlock() == Blocks.GLASS) {
            b = glass;
        } else if (b.getBlock() == Blocks.SAPLING) {
            switch (rand.nextInt(11)) {
                case 0:
                case 1:
                case 2:
                    b = Blocks.RED_FLOWER.getDefaultState();
                    break;
                case 3:
                case 4:
                case 5:
                    b = Blocks.YELLOW_FLOWER.getDefaultState();
                    break;
                case 6:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA);
                    break;
                case 7:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH);
                    break;
                case 8:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK);
                    break;
                case 9:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE);
                    break;
                default:
                    b = air;
                    break;
            }
        }

        if (b == bricks || b.getBlock() == Blocks.STONEBRICK) {
            b = bricks;
            if (rand.nextFloat() < 0.06f) {
                b = bricks_cracked;
            }
        }
        return b;
    }

    public static int getFloor(int height) {
        return (height - 3) % 6;        // -3 instead of -63 because we can also go below the floor
    }

    public static int getLevel(int height) {
        return (height - 63) / 6;
    }

    private boolean isCorner(int x, int z) {
        return (x == 0 && z == 0) || (x == 0 && z == 15) || (x == 15 && z == 0) || (x == 15 && z == 15);
    }

    private boolean isSide(int x, int z) {
        return x == 0 || x == 15 || z == 0 || z == 15;
    }

    private boolean isBorder(int x, int z) {
        return x <= STREETBORDER || x >= (15 - STREETBORDER) || z <= STREETBORDER || z >= (15 - STREETBORDER);
    }

    public static class Explosion {
        private final int radius;
        private final int sqradius;
        private final BlockPos center;

        public Explosion(int radius, BlockPos center) {
            this.radius = radius;
            this.center = center;
            sqradius = radius * radius;
        }

        public int getRadius() {
            return radius;
        }

        public int getSqradius() {
            return sqradius;
        }

        public BlockPos getCenter() {
            return center;
        }
    }

    public static class DamageArea {

        private final long seed;
        private final List<Explosion> explosions = new ArrayList<>();
        private final AxisAlignedBB chunkBox;
        private final boolean damaged[];

        public DamageArea(long seed, int chunkX, int chunkZ) {
            this.seed = seed;
            chunkBox = new AxisAlignedBB(chunkX*16, 0, chunkZ*16, chunkX*16+15, 256, chunkZ*16+15);

            for (int cx = chunkX-5 ; cx <= chunkX+5 ; cx++) {
                for (int cz = chunkZ-5 ; cz <= chunkZ+5 ; cz++) {
                    Explosion explosion = getExplosionAt(cx, cz);
                    if (explosion != null) {
                        if (intersectsWith(explosion.getCenter(), explosion.getRadius())) {
                            explosions.add(explosion);
                        }
                    }
                }
            }
            damaged = new boolean[16*16*256];
            for (int i = 0 ; i < damaged.length ; i++) {
                damaged[i] = false;
            }
        }

        public IBlockState damageBlock(IBlockState b, Random rand, float damage, int index, IBlockState bricks, IBlockState bricks_cracked, IBlockState quartz, IBlockState air) {
            if (rand.nextFloat() <= damage) {
                if (damage < .5f && (b == bricks || b == bricks_cracked || b == quartz)) {
                    if (rand.nextFloat() < .8f) {
                        b = Blocks.IRON_BARS.getDefaultState();
                    } else {
                        damaged[index] = true;
                        b = air;
                    }
                } else {
                    damaged[index] = true;
                    b = air;
                }
            }
            return b;
        }

        private boolean intersectsWith(BlockPos center, int radius) {
            double dmin = distance(center);
            return dmin <= radius * radius;
        }

        private double distance(BlockPos center) {
            double dmin = 0;

            if (center.getX() < chunkBox.minX) {
                dmin += Math.pow(center.getX() - chunkBox.minX, 2);
            } else if (center.getX() > chunkBox.maxX) {
                dmin += Math.pow(center.getX() - chunkBox.maxX, 2);
            }

            if (center.getY() < chunkBox.minY) {
                dmin += Math.pow(center.getY() - chunkBox.minY, 2);
            } else if (center.getY() > chunkBox.maxY) {
                dmin += Math.pow(center.getY() - chunkBox.maxY, 2);
            }

            if (center.getZ() < chunkBox.minZ) {
                dmin += Math.pow(center.getZ() - chunkBox.minZ, 2);
            } else if (center.getZ() > chunkBox.maxZ) {
                dmin += Math.pow(center.getZ() - chunkBox.maxZ, 2);
            }
            return dmin;
        }

        private Explosion getExplosionAt(int chunkX, int chunkZ) {
            Random rand = new Random(seed + chunkZ * 295075153L + chunkX * 797003437L);
            rand.nextFloat();
            rand.nextFloat();
            if (rand.nextFloat() < .005f) {
                return new Explosion(17+rand.nextInt(4*16), new BlockPos(chunkX * 16 + rand.nextInt(16), 70+rand.nextInt(50), chunkZ * 16 + rand.nextInt(16)));
            }
            return null;
        }


        // Get a number indicating how much damage this point should get. 0 Means no damage
        public float getDamage(int x, int y, int z) {
            float damage = 0.0f;
            for (Explosion explosion : explosions) {
                double sq = explosion.getCenter().distanceSq(x, y, z);
                if (sq < explosion.getSqradius()) {
                    double d = Math.sqrt(sq);
                    damage += 3.0f * (explosion.getRadius() - d) / explosion.getRadius();
                }
            }
            return damage;
        }
    }
}
