package com.ratger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class InfoTagsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		NetworkHandler.init();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
				OnPlayerFocus.stopListening();
				TextDisplayManager.players.clear();
				NetworkHandler.sendHandshake();
			}
		);
	}
}