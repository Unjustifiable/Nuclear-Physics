package org.halvors.nuclearphysics.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.halvors.nuclearphysics.common.container.machine.ContainerChemicalExtractor;
import org.halvors.nuclearphysics.common.container.machine.ContainerGasCentrifuge;
import org.halvors.nuclearphysics.common.container.machine.ContainerNuclearBoiler;
import org.halvors.nuclearphysics.common.container.machine.ContainerQuantumAssembler;
import org.halvors.nuclearphysics.common.container.particle.ContainerParticleAccelerator;
import org.halvors.nuclearphysics.common.container.reactor.ContainerReactorCell;
import org.halvors.nuclearphysics.common.tile.machine.TileChemicalExtractor;
import org.halvors.nuclearphysics.common.tile.machine.TileGasCentrifuge;
import org.halvors.nuclearphysics.common.tile.machine.TileNuclearBoiler;
import org.halvors.nuclearphysics.common.tile.machine.TileQuantumAssembler;
import org.halvors.nuclearphysics.common.tile.particle.TileParticleAccelerator;
import org.halvors.nuclearphysics.common.tile.reactor.TileReactorCell;

/**
 * This is the common proxy used by both the client and the server.
 *
 * @author halvors
 */
public class CommonProxy implements IGuiHandler {
	public void preInit() {

	}

	public void init() {

	}

	public void postInit() {

	}

	public void registerBlockRenderer(Block block, IProperty property, String name) {

	}

	public void registerBlockRendererAndIgnore(Block block, IProperty property) {

	}

	public void registerItemRenderer(Item item, int metadata, String id) {

	}

	public void registerItemRenderer(Item item, int metadata, String id, String variant) {

	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		if (tile instanceof TileParticleAccelerator) {
			return new ContainerParticleAccelerator(player.inventory, (TileParticleAccelerator) tile);
		} else if (tile instanceof TileChemicalExtractor) {
			return new ContainerChemicalExtractor(player.inventory, (TileChemicalExtractor) tile);
		} else if (tile instanceof TileGasCentrifuge) {
			return new ContainerGasCentrifuge(player.inventory, (TileGasCentrifuge) tile);
		} else if (tile instanceof TileNuclearBoiler) {
			return new ContainerNuclearBoiler(player.inventory, (TileNuclearBoiler) tile);
		} else if (tile instanceof TileQuantumAssembler) {
			return new ContainerQuantumAssembler(player.inventory, (TileQuantumAssembler) tile);
		} else if (tile instanceof TileReactorCell) {
			return new ContainerReactorCell(player.inventory, (TileReactorCell) tile);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public EntityPlayer getPlayer(MessageContext context) {
		return context.getServerHandler().playerEntity;
	}

	public void addScheduledTask(Runnable runnable, IBlockAccess world) {
		((WorldServer) world).addScheduledTask(runnable);
	}

	public boolean isClient() {
		return false;
	}

	public boolean isServer() {
		return FMLCommonHandler.instance().getSide().isServer();
	}

	public boolean isPaused() {
		return false;
	}
}