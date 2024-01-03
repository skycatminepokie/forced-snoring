package com.skycat.forcedsnoring;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ForcedSnoring implements ModInitializer, EntitySleepEvents.StartSleeping, EntitySleepEvents.StopSleeping, ServerMessageEvents.ChatMessage {
	private static final ArrayList<ServerPlayerEntity> toKick = new ArrayList<>();

	@Override
	public void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
		if (toKick.contains(sender) && message.getSignedContent().toLowerCase().startsWith("zzz")) {
			toKick.remove(sender);
		}
	}

	@Override
	public void onInitialize() {
		EntitySleepEvents.START_SLEEPING.register(this);
		EntitySleepEvents.STOP_SLEEPING.register(this);
		ServerMessageEvents.CHAT_MESSAGE.register(this);
	}

	@Override
	public void onStartSleeping(LivingEntity entity, BlockPos sleepingPos) {
		if (entity instanceof ServerPlayerEntity player) {
			toKick.add(player);
		}
	}

	@Override
	public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
		if (entity instanceof ServerPlayerEntity player) {
			if (toKick.contains(player)) {
				player.networkHandler.disconnect(Text.of("You didn't snore!"));
				toKick.remove(player);
			}
		}
	}
}