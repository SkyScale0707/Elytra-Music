package com.ggscaletest;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ggscaletest.ElytraTotemDesign.ModItems;
import com.ggscaletest.ModEnchantmentEffect.ModEnchantmentEffects;

public class ElytraMusic implements ModInitializer {
	public static final String MOD_ID = "elytra-music";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	// 我们自定义鞘翅图腾状态码
	public static final byte CUSTOM_TOTEM_TRIGGER_STATUS = 114;
	// 鞘翅图腾附魔触发状态码
	public static final byte ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS = 115;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.initializer();
		ModEnchantment.initializer();
		ModEnchantmentEffects.initializer();
		ElytraMusicConfig.load();
		ElytraLootModifier.register();

		LOGGER.info("Hello Fabric world!");
	}
}