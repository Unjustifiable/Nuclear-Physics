package org.halvors.quantum.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.halvors.quantum.Quantum;
import org.halvors.quantum.common.Reference;
import org.halvors.quantum.common.poison.PoisonRadiation;
import org.halvors.quantum.common.transform.vector.Vector3;

import java.util.List;
import java.util.Random;

public class BlockRadioactive extends net.minecraft.block.Block {
    private static final boolean canSpread = true;
    private static final float radius = 5;
    private static final int amplifier = 2;
    private static final boolean canWalkPoison = true;
    private static final boolean isRandomlyRadioactive = true;
    private static final boolean spawnParticle = true;

    private IIcon iconTop;
    private IIcon iconBottom;

    public BlockRadioactive() {
        super(Material.rock);

        setUnlocalizedName("radioactive");
        setTextureName(Reference.PREFIX + "radioactive");
        setCreativeTab(Quantum.getCreativeTab());
        setTickRandomly(true);
        setHardness(0.2F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);

        iconTop = iconRegister.registerIcon(Reference.PREFIX + getUnlocalizedName().replace("tile.", "") + "_top");
        iconBottom = iconRegister.registerIcon(Reference.PREFIX + getUnlocalizedName().replace("tile.", "") + "_bottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        switch (side) {
            case 0:
                return iconBottom;

            case 1:
                return iconTop;

            default:
                return blockIcon;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (spawnParticle) {
            if (Minecraft.getMinecraft().gameSettings.particleSetting == 0) {
                int radius = 3;

                for (int i = 0; i < 2; i++) {
                    Vector3 pos = new Vector3(x, y, z);
                    pos.add(random.nextDouble() * radius - radius / 2);

                    EntitySmokeFX fx = new EntitySmokeFX(world, pos.x, pos.y, pos.z, (random.nextDouble() - 0.5D) / 2.0D, (random.nextDouble() - 0.5D) / 2.0D, (random.nextDouble() - 0.5D) / 2.0D);
                    fx.setRBGColorF(0.2F, 0.8F, 0);
                    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                }
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (isRandomlyRadioactive) {
                AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
                List<EntityLivingBase> entitiesNearby = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);

                for (EntityLivingBase entity : entitiesNearby) {
                    PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), entity, amplifier);
                }
            }

            if (canSpread) {
                for (int i = 0; i < 4; i++) {
                    int newX = x + random.nextInt(3) - 1;
                    int newY = y + random.nextInt(5) - 3;
                    int newZ = z + random.nextInt(3) - 1;
                    net.minecraft.block.Block block = world.getBlock(newX, newY, newZ);

                    if (random.nextFloat() > 0.4 && (block == Blocks.farmland || block == Blocks.grass)) {
                        world.setBlock(newX, newY, newZ, this);
                    }
                }

                if (random.nextFloat() > 0.85) {
                    world.setBlock(x, y, z, Blocks.dirt);
                }
            }
        }
    }

    /**
     * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
     */
    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityLiving && canWalkPoison) {
            PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), (EntityLiving) entity);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
}