package org.halvors.nuclearphysics.common.worldgen;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.halvors.nuclearphysics.common.ConfigurationManager.General;
import org.halvors.nuclearphysics.common.init.ModBlocks;

import java.util.Random;

public class OreGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!(chunkGenerator instanceof ChunkProviderHell) && !(chunkGenerator instanceof ChunkProviderEnd) && General.enableOreRegeneration) {
            for (int i = 0; i < General.uraniumPerChunk; i++) {
                BlockPos pos = new BlockPos(chunkX * 16 + random.nextInt(16), random.nextInt(25), (chunkZ * 16) + random.nextInt(16));
                new WorldGenMinable(ModBlocks.blockUraniumOre.getDefaultState(), 3, BlockMatcher.forBlock(Blocks.STONE)).generate(world, random, pos);
            }
        }
    }
}
