package org.halvors.quantum.common.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/* Creates a slot with a specific amount of items that matches the slot's requirements. Allows easy
 * shift right clicking management and slot blocking in classes. In your container you can use
 * this.getSlot(i).isItemValid to justify the player's shift clicking actions to match the slot.
 */
public class SlotItemHandlerSpecificItem extends SlotItemHandler {
    private ItemStack[] validItemStacks = new ItemStack[0];
    private Class[] validClasses = new Class[0];

    private boolean isInverted = false;
    private boolean isMetadataSensitive = false;

    public SlotItemHandlerSpecificItem(IItemHandler inventory, int index, int x, int y, ItemStack... itemStacks) {
        super(inventory, index, x, y);

        setItemStacks(itemStacks);
    }

    public SlotItemHandlerSpecificItem(IItemHandler inventory, int index, int x, int y, Class... validClasses) {
        super(inventory, index, x, y);

        setClasses(validClasses);
    }

    public void setItemStacks(ItemStack... validItemStacks) {
        this.validItemStacks = validItemStacks;

    }

    public void setClasses(Class... validClasses) {
        this.validClasses = validClasses;

    }

    public void setMetadataSensitive() {
        isMetadataSensitive = true;
    }

    public void toggleInverted() {
        isInverted = !isInverted;
    }

    // Check if the stack is a valid item for this slot. Always true beside for the armor slots.
    @Override
    public boolean isItemValid(ItemStack compareStack) {
        boolean returnValue = false;

        for (ItemStack itemStack : validItemStacks) {
            if (compareStack.isItemEqual(itemStack) || (!isMetadataSensitive && compareStack == itemStack)) {
                returnValue = true;
                break;
            }
        }

        if (!returnValue) {
            for (Class clazz : validClasses) {
                if (clazz.equals(compareStack.getItem().getClass()) || clazz.isInstance(compareStack.getItem())) {
                    returnValue = true;

                    break;
                }
            }
        }

        if (isInverted) {
            return !returnValue;
        }

        return returnValue;
    }
}
