package org.halvors.quantum.common.event.handler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.halvors.quantum.api.explosion.ExplosionEvent;
import org.halvors.quantum.common.tile.particle.TileFulmination;
import org.halvors.quantum.common.utility.transform.vector.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FulminationHandler {
    public static final FulminationHandler INSTANCE = new FulminationHandler();
    public static final List<TileFulmination> list = new ArrayList<>();

    public void register(TileFulmination tile) {
        if (!list.contains(tile)) {
            list.add(tile);
        }
    }

    public void unregister(TileFulmination tileEntity) {
        list.remove(tileEntity);
    }

    @SubscribeEvent
    public void onExplosionEvent(ExplosionEvent.DoExplosionEvent event) {
        if (event.iExplosion != null) {
            if (event.iExplosion.getRadius() > 0 && event.iExplosion.getEnergy() > 0) {
                HashSet<TileFulmination> avaliableGenerators = new HashSet<>();

                for (TileFulmination tileEntity : FulminationHandler.list) {
                    if (tileEntity != null) {
                        if (!tileEntity.isInvalid()) {
                            Vector3 tileDiDian = new Vector3(tileEntity);
                            tileDiDian.translate(0.5f);
                            double juLi = tileDiDian.distance(new Vector3(event.x, event.y, event.z));

                            if (juLi <= event.iExplosion.getRadius() && juLi > 0) {
                                float miDu = 0; //event.world.getBlockDensity(new Vec3d(event.x, event.y, event.z), QuantumBlocks.blockFulmination.getCollisionBoundingBox(event.world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));

                                if (miDu < 1) {
                                    avaliableGenerators.add(tileEntity);
                                }
                            }
                        }
                    }
                }

                final float totalEnergy = event.iExplosion.getEnergy();
                final float maxEnergyPerGenerator = totalEnergy / avaliableGenerators.size();

                for (TileFulmination tile : avaliableGenerators) {
                    //float density = event.world.getBlockDensity(new Vec3d(event.x, event.y, event.z), QuantumBlocks.blockFulmination.getCollisionBoundingBox(event.world, tile.getPos()));
                    float density = 0;
                    double juLi = new Vector3(tile).distance(new Vector3(event.x, event.y, event.z));
                    long energy = (long) Math.min(maxEnergyPerGenerator, maxEnergyPerGenerator / (juLi / event.iExplosion.getRadius()));
                    energy = (long) Math.max((1 - density) * energy, 0);

                    tile.getEnergyStorage().receiveEnergy((int) energy, false);
                }
            }
        }
    }
}
