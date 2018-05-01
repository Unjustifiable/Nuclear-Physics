package org.halvors.nuclearphysics.common.item.tool;

import buildcraft.api.tools.IToolWrench;
import cofh.api.item.IToolHammer;
import mekanism.api.IMekWrench;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.api.item.IWrench;
import org.halvors.nuclearphysics.common.Integration;
import org.halvors.nuclearphysics.common.Reference;
import org.halvors.nuclearphysics.common.item.ItemBase;
import org.halvors.nuclearphysics.common.type.Color;
import org.halvors.nuclearphysics.common.utility.InventoryUtility;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

@InterfaceList({
        @Interface(iface = "buildcraft.api.tools.IToolWrench", modid = Integration.BUILDCRAFT_CORE_ID),
        @Interface(iface = "cofh.api.item.IToolHammer", modid = Integration.COFH_CORE_ID),
        @Interface(iface = "mekanism.api.IMekWrench", modid = Integration.MEKANISM_ID)
})
public class ItemWrench extends ItemBase implements IWrench, IToolWrench, IToolHammer, IMekWrench {
    public ItemWrench() {
        super("wrench");

        setMaxStackSize(1);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull final ItemStack itemStack, @Nonnull final EntityPlayer player, @Nonnull final List<String> list, final boolean flag) {
        final WrenchState state = getState(itemStack);

        list.add(LanguageUtility.transelate("tooltip.state") + ": " + state.getColor() + state.getName());

        super.addInformation(itemStack, player, list, flag);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull final ItemStack itemStack, final World world, final EntityPlayer player, final EnumHand hand) {
        if (player.isSneaking()) {
            WrenchState state = getState(itemStack);
            final int toSet = state.ordinal() < WrenchState.values().length - 1 ? state.ordinal() + 1 : 0;
            setState(itemStack, WrenchState.values()[toSet]);
            state = getState(itemStack);

            if (!world.isRemote) {
                player.sendMessage(new TextComponentString(Color.DARK_BLUE + "[" + Reference.NAME + "] " + Color.GREY + LanguageUtility.transelate("tooltip.state") + ": " + state.getColor() + state.getName()));

                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, itemStack);
    }

    @Override
    public EnumActionResult onItemUse(final ItemStack itemStack, final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        final IBlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        switch (getState(itemStack)) {
            case ROTATE:
                final EnumFacing[] validRotations = block.getValidRotations(world, pos);

                if (validRotations != null && validRotations.length > 0) { // NOTE: Null check here is actually needed, otherwise it causes game crash.
                    final List<EnumFacing> validRotationsList = Arrays.asList(validRotations);

                    if (!player.isSneaking() && validRotationsList.contains(facing)) {
                        block.rotateBlock(world, pos, facing);
                    } else if (player.isSneaking() && validRotationsList.contains(facing.getOpposite())) {
                        block.rotateBlock(world, pos, facing.getOpposite());
                    }
                }

                return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(final ItemStack itemStack, final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return getState(itemStack) == WrenchState.WRENCH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canUseWrench(final ItemStack itemStack, final EntityPlayer player, final BlockPos pos) {
        return getState(itemStack) == WrenchState.WRENCH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canWrench(final EntityPlayer player, final EnumHand hand, final ItemStack itemStack, final RayTraceResult rayTrace) {
        return getState(itemStack) == WrenchState.WRENCH;
    }

    @Override
    public void wrenchUsed(final EntityPlayer player, final EnumHand hand, final ItemStack itemStack, final RayTraceResult rayTrace) {
        player.swingArm(hand);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isUsable(final ItemStack itemStack, final EntityLivingBase entityLiving, final BlockPos pos) {
        return getState(itemStack) == WrenchState.WRENCH;
    }

    @Override
    public boolean isUsable(final ItemStack itemStack, final EntityLivingBase entityLiving, final Entity entity) {
        return getState(itemStack) == WrenchState.WRENCH;
    }

    @Override
    public void toolUsed(final ItemStack itemStack, final EntityLivingBase entityLiving, final BlockPos pos) {

    }

    @Override
    public void toolUsed(final ItemStack itemStack, final EntityLivingBase entityLiving, final Entity entity) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private WrenchState getState(final ItemStack itemStack) {
        return WrenchState.values()[InventoryUtility.getNBTTagCompound(itemStack).getInteger("state")];
    }

    private void setState(final ItemStack itemStack, final WrenchState state) {
        InventoryUtility.getNBTTagCompound(itemStack).setInteger("state", state.ordinal());
    }

    public enum WrenchState {
        WRENCH("wrench", Color.BRIGHT_GREEN),
        ROTATE("rotate", Color.YELLOW);

        private final String name;
        private final Color color;

        WrenchState(final String name, final Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return LanguageUtility.transelate("tooltip." + name);
        }

        public Color getColor() {
            return color;
        }
    }
}