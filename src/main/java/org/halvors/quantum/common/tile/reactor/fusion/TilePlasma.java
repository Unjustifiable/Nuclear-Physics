package org.halvors.quantum.common.tile.reactor.fusion;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.halvors.quantum.common.event.PlasmaEvent;
import org.halvors.quantum.common.grid.thermal.ThermalGrid;
import org.halvors.quantum.common.utility.transform.vector.Vector3;
import org.halvors.quantum.common.utility.transform.vector.VectorWorld;

public class TilePlasma extends TileEntity implements ITickable {
    public static int plasmaMaxTemperature = 1000000;
    private int temperature = plasmaMaxTemperature;

    @Override
    public void update() {
        ThermalGrid.addTemperature(new VectorWorld(world, new Vector3(pos.getX(), pos.getY(), pos.getZ())), (temperature - ThermalGrid.getTemperature(new VectorWorld(world, new Vector3(pos.getX(), pos.getY(), pos.getZ()))) * 0.1F));

        if (world.getWorldTime() % 20 == 0) {
            temperature /= 1.5;

            if (temperature <= plasmaMaxTemperature / 10) {
                // At this temperature, set block to fire.
                world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
            } else {
                for (EnumFacing side : EnumFacing.VALUES) {
                    // Randomize spread direction.
                    if (world.rand.nextFloat() < 0.4) {
                        final BlockPos spreadPos = pos.offset(side);
                        final TileEntity tile = world.getTileEntity(spreadPos);

                        if (!(tile instanceof TilePlasma)) {
                            MinecraftForge.EVENT_BUS.post(new PlasmaEvent.PlasmaSpawnEvent(world, spreadPos, temperature));
                        }
                    }
                }
            }
        }
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
