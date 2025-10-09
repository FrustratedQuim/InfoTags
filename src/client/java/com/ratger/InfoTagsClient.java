package com.ratger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class InfoTagsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		NetworkHandler.init();
		TextRenderManager.init();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
				OnPlayerFocus.stopListening();
				TextRenderManager.players.clear();
				NetworkHandler.sendHandshake();
			}
		);
	}
}