package org.halvors.quantum.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import org.halvors.quantum.Quantum;

public class ItemBlockQuantum extends ItemBlock {
    public ItemBlockQuantum(Block block) {
        super(block);

        setCreativeTab(Quantum.getCreativeTab());
    }
}