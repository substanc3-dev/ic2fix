package dev.substanc3.ic2fix;

import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.ref.Ic2Fluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IC2FixMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ic2fix");

	public static final List<BlockEntityType> IC2_BLOCK_ENTITIES = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		LOGGER.info("Client init!");
		Ic2Fluids.init();
	}

	@Override
	public void onInitialize() {
		IC2.achievements = new IC2Achievements();
	}
}
