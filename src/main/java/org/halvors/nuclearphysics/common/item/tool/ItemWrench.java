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
import org.halvors.nuclearphysics.common.type.EnumColor;
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
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean flag) {
        EnumWrenchState state = getState(itemStack);

        list.add(LanguageUtility.transelate("tooltip.state") + ": " + state.getColor() + state.getName());

        super.addInformation(itemStack, player, list, flag);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            EnumWrenchState state = getState(itemStack);
            int toSet = state.ordinal() < EnumWrenchState.values().length - 1 ? state.ordinal() + 1 : 0;
            setState(itemStack, EnumWrenchState.values()[toSet]);
            state = getState(itemStack);

            if (!world.isRemote) {
                player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + "[" + Reference.NAME + "] " + EnumColor.GREY + LanguageUtility.transelate("tooltip.state") + ": " + state.getColor() + state.getName()));

                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, itemStack);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (getState(itemStack) == EnumWrenchState.ROTATE) {
            EnumFacing[] validRotations = block.getValidRotations(world, pos);

            if (validRotations != null && validRotations.length > 0) { // NOTE: Null check here is actually needed, otherwise it causes game crash.
                List<EnumFacing> validRotationsList = Arrays.asList(validRotations);

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
    public boolean doesSneakBypassUse(ItemStack itemStack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return getState(itemStack) == EnumWrenchState.WRENCH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canUseWrench(ItemStack itemStack, EntityPlayer player, BlockPos pos) {
        return getState(itemStack) == EnumWrenchState.WRENCH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canWrench(EntityPlayer player, EnumHand hand, ItemStack itemStack, RayTraceResult rayTrace) {
        return getState(itemStack) == EnumWrenchState.WRENCH;
    }

    @Override
    public void wrenchUsed(EntityPlayer player, EnumHand hand, ItemStack itemStack, RayTraceResult rayTrace) {
        player.swingArm(hand);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isUsable(ItemStack itemStack, EntityLivingBase entityLiving, BlockPos pos) {
        return getState(itemStack) == EnumWrenchState.WRENCH;
    }

    @Override
    public boolean isUsable(ItemStack itemStack, EntityLivingBase entityLiving, Entity entity) {
        return getState(itemStack) == EnumWrenchState.WRENCH;
    }

    @Override
    public void toolUsed(ItemStack itemStack, EntityLivingBase entityLiving, BlockPos pos) {

    }

    @Override
    public void toolUsed(ItemStack itemStack, EntityLivingBase entityLiving, Entity entity) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private EnumWrenchState getState(ItemStack itemStack) {
        return EnumWrenchState.values()[InventoryUtility.getNBTTagCompound(itemStack).getInteger("state")];
    }

    private void setState(ItemStack itemStack, EnumWrenchState state) {
        InventoryUtility.getNBTTagCompound(itemStack).setInteger("state", state.ordinal());
    }

    public enum EnumWrenchState {
        WRENCH(EnumColor.BRIGHT_GREEN),
        ROTATE(EnumColor.YELLOW);

        private final EnumColor color;

        EnumWrenchState(EnumColor color) {
            this.color = color;
        }

        public String getName() {
            return LanguageUtility.transelate("tooltip." + name().toLowerCase());
        }

        public EnumColor getColor() {
            return color;
        }
    }
}