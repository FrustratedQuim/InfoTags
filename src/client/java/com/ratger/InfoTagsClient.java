package com.ratger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;

public class InfoTagsClient implements ClientModInitializer {
	private ClientWorld lastWorld = null;
	private boolean initialHandshakeSent = false;

	@Override
	public void onInitializeClient() {
		NetworkHandler.initialize();
		OnPlayerFocus.startListening();

		// Инициализация проверки связи с сервером при смене мира
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world != null && lastWorld != client.world) {
				if (lastWorld != null) {
					NetworkHandler.resetHandshakeAndCache();
					initialHandshakeSent = false;
				}
				lastWorld = client.world;
			}

			if (client.world != null && client.player != null && !initialHandshakeSent) {
				NetworkHandler.sendHandshake();
				initialHandshakeSent = true;
			}

			NetworkHandler.checkHandshakeTimeout();
		});
	}
}