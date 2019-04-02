package org.halvors.nuclearphysics.common.item.reactor.fission;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.common.item.ItemRadioactive;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemFuel extends ItemRadioactive {
    public static final int decay = 2500;

    // The energy in one KG of uranium is: 72PJ, 100TJ in one cell of uranium.
    public static final long energyDensity = 100000000000L;

    // Approximately 20,000,000J per tick. 400 MW.
    public static final long energyPerTick = energyDensity / 50000;

    public ItemFuel(String name) {
        super(name);

        setMaxStackSize(1);
        setMaxDamage(decay);
        setNoRepair();
    }

    @SuppressWarnings("deprecation")
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item));
        list.add(new ItemStack(item, 1, getMaxDamage() - 1));
    }
}