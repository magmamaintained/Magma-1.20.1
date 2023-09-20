package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import org.apache.commons.lang3.Validate;
import org.bukkit.World;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftBlockVector;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockVector;

public class CraftStructureBlock extends CraftBlockEntityState<StructureBlockEntity> implements Structure {

    private static final int MAX_SIZE = 48;

    public CraftStructureBlock(World world, StructureBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public String getStructureName() {
        return getSnapshot().getStructureName();
    }

    @Override
    public void setStructureName(String name) {
        Preconditions.checkArgument(name != null, "Structure name cannot be null");
        getSnapshot().setStructureName(name);
    }

    @Override
    public String getAuthor() {
        return getSnapshot().author;
    }

    @Override
    public void setAuthor(String author) {
        Preconditions.checkArgument(author != null, "Author name cannot be null");
        Preconditions.checkArgument(!author.isEmpty(), "Author name cannot be empty");
        getSnapshot().author = author;
    }

    @Override
    public void setAuthor(LivingEntity entity) {
        Preconditions.checkArgument(entity != null, "Structure Block author entity cannot be null");
        getSnapshot().createdBy(((CraftLivingEntity) entity).getHandle());
    }

    @Override
    public BlockVector getRelativePosition() {
        return CraftBlockVector.toBukkit(getSnapshot().structurePos);
    }

    @Override
    public void setRelativePosition(BlockVector vector) {
        Preconditions.checkArgument(isBetween(vector.getBlockX(), -MAX_SIZE, MAX_SIZE), "Structure Size (X) must be between -%s and %s but got %s", MAX_SIZE, MAX_SIZE, vector.getBlockX());
        Preconditions.checkArgument(isBetween(vector.getBlockY(), -MAX_SIZE, MAX_SIZE), "Structure Size (Y) must be between -%s and %s but got %s", MAX_SIZE, MAX_SIZE, vector.getBlockY());
        Preconditions.checkArgument(isBetween(vector.getBlockZ(), -MAX_SIZE, MAX_SIZE), "Structure Size (Z) must be between -%s and %s but got %s", MAX_SIZE, MAX_SIZE, vector.getBlockZ());
        getSnapshot().structurePos = CraftBlockVector.toBlockPosition(vector);
    }

    @Override
    public BlockVector getStructureSize() {
        return CraftBlockVector.toBukkit(getSnapshot().structureSize);
    }

    @Override
    public void setStructureSize(BlockVector vector) {
        Preconditions.checkArgument(isBetween(vector.getBlockX(), 0, MAX_SIZE), "Structure Size (X) must be between %s and %s but got %s", 0, MAX_SIZE, vector.getBlockX());
        Preconditions.checkArgument(isBetween(vector.getBlockY(), 0, MAX_SIZE), "Structure Size (Y) must be between %s and %s but got %s", 0, MAX_SIZE, vector.getBlockY());
        Preconditions.checkArgument(isBetween(vector.getBlockZ(), 0, MAX_SIZE), "Structure Size (Z) must be between %s and %s but got %s", 0, MAX_SIZE, vector.getBlockZ());
        getSnapshot().structureSize = CraftBlockVector.toBlockPosition(vector);
    }

    @Override
    public void setMirror(Mirror mirror) {
        Preconditions.checkArgument(mirror != null, "Mirror cannot be null");
        getSnapshot().mirror = net.minecraft.world.level.block.Mirror.valueOf(mirror.name());
    }

    @Override
    public Mirror getMirror() {
        return Mirror.valueOf(getSnapshot().mirror.name());
    }

    @Override
    public void setRotation(StructureRotation rotation) {
        Preconditions.checkArgument(rotation != null, "StructureRotation cannot be null");
        getSnapshot().rotation = Rotation.valueOf(rotation.name());
    }

    @Override
    public StructureRotation getRotation() {
        return StructureRotation.valueOf(getSnapshot().rotation.name());
    }

    @Override
    public void setUsageMode(UsageMode mode) {
        Preconditions.checkArgument(mode != null, "UsageMode cannot be null");
        getSnapshot().mode = StructureMode.valueOf(mode.name());
    }

    @Override
    public UsageMode getUsageMode() {
        return UsageMode.valueOf(getSnapshot().getMode().name());
    }

    @Override
    public void setIgnoreEntities(boolean flag) {
        getSnapshot().ignoreEntities = flag;
    }

    @Override
    public boolean isIgnoreEntities() {
        return getSnapshot().ignoreEntities;
    }

    @Override
    public void setShowAir(boolean showAir) {
        getSnapshot().showAir = showAir;
    }

    @Override
    public boolean isShowAir() {
        return getSnapshot().showAir;
    }

    @Override
    public void setBoundingBoxVisible(boolean showBoundingBox) {
        getSnapshot().showBoundingBox = showBoundingBox;
    }

    @Override
    public boolean isBoundingBoxVisible() {
        return getSnapshot().showBoundingBox;
    }

    @Override
    public void setIntegrity(float integrity) {
        Preconditions.checkArgument(isBetween(integrity, 0.0f, 1.0f), "Integrity must be between 0.0f and 1.0f but got %s", integrity);
        getSnapshot().integrity = integrity;
    }

    @Override
    public float getIntegrity() {
        return getSnapshot().integrity;
    }

    @Override
    public void setSeed(long seed) {
        getSnapshot().seed = seed;
    }

    @Override
    public long getSeed() {
        return getSnapshot().seed;
    }

    @Override
    public void setMetadata(String metadata) {
        Preconditions.checkArgument(metadata != null, "Structure metadata cannot be null");
        if (getUsageMode() == UsageMode.DATA) {
            getSnapshot().metaData = metadata;
        }
    }

    @Override
    public String getMetadata() {
        return getSnapshot().metaData;
    }

    @Override
    protected void applyTo(StructureBlockEntity tileEntity) {
        super.applyTo(tileEntity);
        net.minecraft.world.level.LevelAccessor access = getWorldHandle();

        // Ensure block type is correct
        if (access instanceof net.minecraft.world.level.Level) {
            tileEntity.setMode(tileEntity.getMode());
        } else if (access != null) {
            // Custom handle during world generation
            // From TileEntityStructure#setUsageMode(BlockPropertyStructureMode)
            net.minecraft.world.level.block.state.BlockState data = access.getBlockState(this.getPosition());
            if (data.is(net.minecraft.world.level.block.Blocks.STRUCTURE_BLOCK)) {
                access.setBlock(this.getPosition(), data.setValue(net.minecraft.world.level.block.StructureBlock.MODE, tileEntity.getMode()), 2);
            }
        }
    }

    private static boolean isBetween(int num, int min, int max) {
        return num >= min && num <= max;
    }

    private static boolean isBetween(float num, float min, float max) {
        return num >= min && num <= max;
    }
}
