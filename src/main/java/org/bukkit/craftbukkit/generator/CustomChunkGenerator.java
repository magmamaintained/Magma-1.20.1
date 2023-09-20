package org.bukkit.craftbukkit.generator;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftHeightMap;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.RandomSourceWrapper;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CustomChunkGenerator extends InternalChunkGenerator {

    private final net.minecraft.world.level.chunk.ChunkGenerator delegate;
    private final ChunkGenerator generator;
    private final ServerLevel world;
    private final Random random = new Random();
    private boolean newApi;
    private boolean implementBaseHeight = true;

    @Deprecated
    private class CustomBiomeGrid implements BiomeGrid {

        private final ChunkAccess biome;

        public CustomBiomeGrid(ChunkAccess biome) {
            this.biome = biome;
        }

        @Override
        public Biome getBiome(int x, int z) {
            return getBiome(x, 0, z);
        }

        @Override
        public void setBiome(int x, int z, Biome bio) {
            for (int y = world.getWorld().getMinHeight(); y < world.getWorld().getMaxHeight(); y += 4) {
                setBiome(x, y, z, bio);
            }
        }

        @Override
        public Biome getBiome(int x, int y, int z) {
            return CraftBlock.biomeBaseToBiome(biome.biomeRegistry, biome.getNoiseBiome(x >> 2, y >> 2, z >> 2));
        }

        @Override
        public void setBiome(int x, int y, int z, Biome bio) {
            Preconditions.checkArgument(bio != Biome.CUSTOM, "Cannot set the biome to %s", bio);
            biome.setBiome(x >> 2, y >> 2, z >> 2, CraftBlock.biomeToBiomeBase(biome.biomeRegistry, bio));
        }
    }

    public CustomChunkGenerator(ServerLevel world, net.minecraft.world.level.chunk.ChunkGenerator delegate, ChunkGenerator generator) {
        super(delegate.getBiomeSource(), delegate.generationSettingsGetter);

        this.world = world;
        this.delegate = delegate;
        this.generator = generator;
    }

    public net.minecraft.world.level.chunk.ChunkGenerator getDelegate() {
        return delegate;
    }

    private static WorldgenRandom getSeededRandom() {
        return new WorldgenRandom(new LegacyRandomSource(0));
    }

    @Override
    public BiomeSource getBiomeSource() {
        return delegate.getBiomeSource();
    }

    @Override
    public int getMinY() {
        return delegate.getMinY();
    }

    @Override
    public int getSeaLevel() {
        return delegate.getSeaLevel();
    }

    @Override
    public void createStructures(RegistryAccess iregistrycustom, ChunkGeneratorStructureState chunkgeneratorstructurestate, StructureManager structuremanager, ChunkAccess ichunkaccess, StructureTemplateManager structuretemplatemanager) {
        WorldgenRandom random = getSeededRandom();
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;

        random.setSeed(Mth.getSeed(x, "should-structures".hashCode(), z) ^ world.getSeed());
        if (generator.shouldGenerateStructures(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
            super.createStructures(iregistrycustom, chunkgeneratorstructurestate, structuremanager, ichunkaccess, structuretemplatemanager);
        }
    }

    @Override
    public void buildSurface(WorldGenRegion regionlimitedworldaccess, StructureManager structuremanager, RandomState randomstate, ChunkAccess ichunkaccess) {
        WorldgenRandom random = getSeededRandom();
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;

        random.setSeed(Mth.getSeed(x, "should-surface".hashCode(), z) ^ regionlimitedworldaccess.getSeed());
        if (generator.shouldGenerateSurface(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
            delegate.buildSurface(regionlimitedworldaccess, structuremanager, randomstate, ichunkaccess);
        }

        CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess);

        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        generator.generateSurface(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);

        if (generator.shouldGenerateBedrock()) {
            random = getSeededRandom();
            random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
            // delegate.buildBedrock(ichunkaccess, random);
        }

        random = getSeededRandom();
        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        generator.generateBedrock(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
        chunkData.breakLink();

        // return if new api is used
        if (newApi) {
            return;
        }

        // old ChunkGenerator logic, for backwards compatibility
        // Call the bukkit ChunkGenerator before structure generation so correct biome information is available.
        this.random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

        // Get default biome data for chunk
        CustomBiomeGrid biomegrid = new CustomBiomeGrid(ichunkaccess);

        ChunkData data;
        try {
            if (generator.isParallelCapable()) {
                data = generator.generateChunkData(this.world.getWorld(), this.random, x, z, biomegrid);
            } else {
                synchronized (this) {
                    data = generator.generateChunkData(this.world.getWorld(), this.random, x, z, biomegrid);
                }
            }
        } catch (UnsupportedOperationException exception) {
            newApi = true;
            return;
        }

        Preconditions.checkArgument(data instanceof OldCraftChunkData, "Plugins must use createChunkData(World) rather than implementing ChunkData: %s", data);
        OldCraftChunkData craftData = (OldCraftChunkData) data;
        LevelChunkSection[] sections = craftData.getRawChunkData();

        LevelChunkSection[] csect = ichunkaccess.getSections();
        int scnt = Math.min(csect.length, sections.length);

        // Loop through returned sections
        for (int sec = 0; sec < scnt; sec++) {
            if (sections[sec] == null) {
                continue;
            }
            LevelChunkSection section = sections[sec];

            // SPIGOT-6843: Copy biomes over to new section.
            // Not the most performant way, but has a small footprint and developer should move to the new api anyway
            LevelChunkSection oldSection = csect[sec];
            for (int biomeX = 0; biomeX < 4; biomeX++) {
                for (int biomeY = 0; biomeY < 4; biomeY++) {
                    for (int biomeZ = 0; biomeZ < 4; biomeZ++) {
                        section.setBiome(biomeX, biomeY, biomeZ, oldSection.getNoiseBiome(biomeX, biomeY, biomeZ));
                    }
                }
            }

            csect[sec] = section;
        }

        if (craftData.getTiles() != null) {
            for (BlockPos pos : craftData.getTiles()) {
                int tx = pos.getX();
                int ty = pos.getY();
                int tz = pos.getZ();
                net.minecraft.world.level.block.state.BlockState block = craftData.getTypeId(tx, ty, tz);

                if (block.hasBlockEntity()) {
                    BlockEntity tile = ((EntityBlock) block.getBlock()).newBlockEntity(new BlockPos((x << 4) + tx, ty, (z << 4) + tz), block);
                    ichunkaccess.setBlockEntity(tile);
                }
            }
        }
    }

    @Override
    public void applyCarvers(WorldGenRegion regionlimitedworldaccess, long seed, RandomState randomstate, BiomeManager biomemanager, StructureManager structuremanager, ChunkAccess ichunkaccess, GenerationStep.Carving worldgenstage_features) {
        WorldgenRandom random = getSeededRandom();
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;

        random.setSeed(Mth.getSeed(x, "should-caves".hashCode(), z) ^ regionlimitedworldaccess.getSeed());
        if (generator.shouldGenerateCaves(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
            delegate.applyCarvers(regionlimitedworldaccess, seed, randomstate, biomemanager, structuremanager, ichunkaccess, worldgenstage_features);
        }

        // Minecraft removed the LIQUID_CARVERS stage from world generation, without removing the LIQUID Carving enum.
        // Meaning this method is only called once for each chunk, so no check is required.
        CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess);
        random.setDecorationSeed(seed, 0, 0);
        generator.generateCaves(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
        chunkData.breakLink();
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomstate, StructureManager structuremanager, ChunkAccess ichunkaccess) {
        CompletableFuture<ChunkAccess> future = null;
        WorldgenRandom random = getSeededRandom();
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;

        random.setSeed(Mth.getSeed(x, "should-noise".hashCode(), z) ^ this.world.getSeed());
        if (generator.shouldGenerateNoise(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
            future = delegate.fillFromNoise(executor, blender, randomstate, structuremanager, ichunkaccess);
        }

        java.util.function.Function<ChunkAccess, ChunkAccess> function = (ichunkaccess1) -> {
            CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess1);
            random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

            generator.generateNoise(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
            chunkData.breakLink();
            return ichunkaccess1;
        };

        return future == null ? CompletableFuture.supplyAsync(() -> function.apply(ichunkaccess), net.minecraft.Util.backgroundExecutor()) : future.thenApply(function);
    }

    @Override
    public int getBaseHeight(int i, int j, Heightmap.Types heightmap_type, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
        if (implementBaseHeight) {
            try {
                WorldgenRandom random = getSeededRandom();
                int xChunk = i >> 4;
                int zChunk = j >> 4;
                random.setSeed((long) xChunk * 341873128712L + (long) zChunk * 132897987541L);

                return generator.getBaseHeight(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), i, j, CraftHeightMap.fromNMS(heightmap_type));
            } catch (UnsupportedOperationException exception) {
                implementBaseHeight = false;
            }
        }

        return delegate.getBaseHeight(i, j, heightmap_type, levelheightaccessor, randomstate);
    }

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<net.minecraft.world.level.biome.Biome> holder, StructureManager structuremanager, MobCategory enumcreaturetype, BlockPos blockposition) {
        return delegate.getMobsAt(holder, structuremanager, enumcreaturetype, blockposition);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel generatoraccessseed, ChunkAccess ichunkaccess, StructureManager structuremanager) {
        WorldgenRandom random = getSeededRandom();
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;

        random.setSeed(Mth.getSeed(x, "should-decoration".hashCode(), z) ^ generatoraccessseed.getSeed());
        super.applyBiomeDecoration(generatoraccessseed, ichunkaccess, structuremanager, generator.shouldGenerateDecorations(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z));
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomstate, BlockPos blockposition) {
        delegate.addDebugScreenInfo(list, randomstate, blockposition);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion regionlimitedworldaccess) {
        WorldgenRandom random = getSeededRandom();
        int x = regionlimitedworldaccess.getCenter().x;
        int z = regionlimitedworldaccess.getCenter().z;

        random.setSeed(Mth.getSeed(x, "should-mobs".hashCode(), z) ^ regionlimitedworldaccess.getSeed());
        if (generator.shouldGenerateMobs(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
            delegate.spawnOriginalMobs(regionlimitedworldaccess);
        }
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return delegate.getSpawnHeight(levelheightaccessor);
    }

    @Override
    public int getGenDepth() {
        return delegate.getGenDepth();
    }

    @Override
    public NoiseColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
        return delegate.getBaseColumn(i, j, levelheightaccessor, randomstate);
    }

    @Override
    protected Codec<? extends net.minecraft.world.level.chunk.ChunkGenerator> codec() {
        return Codec.unit(null);
    }
}
