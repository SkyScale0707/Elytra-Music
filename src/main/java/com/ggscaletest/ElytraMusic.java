package com.ggscaletest;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ggscaletest.ElytraTotemEnchantments.ModEnchantments;
import com.ggscaletest.ElytraTotemItems.ModItems;

public class ElytraMusic implements ModInitializer {
	public static final String MOD_ID = "elytra-music";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final byte CUSTOM_TOTEM_TRIGGER_STATUS = 114;

	public static final byte ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS = 115;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.initializer();
		ModEnchantments.initializer();
		ElytraMusicConfig.load();
		ModLootModifier.register();

		LOGGER.info("Hello Fabric world!");
	}
}
