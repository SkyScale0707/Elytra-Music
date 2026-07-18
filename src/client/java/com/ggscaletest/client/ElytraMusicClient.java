package com.ggscaletest.client;

import com.ggscaletest.client.ModClientEffect.CustomElytraBrokenRender;

import net.fabricmc.api.ClientModInitializer;

public class ElytraMusicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as
		// rendering.
		ElytraTooltip.register();
		CustomElytraBrokenRender.initializer();
		// 将1.21.1版本的load方法新开一个类LoadCustomMusic存储
		LoadCustomMusic.loadCustomMusics();

	}
}