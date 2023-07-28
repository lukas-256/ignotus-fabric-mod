package com.ignotusmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Shadow;

public class AutoJumpModClient implements ClientModInitializer {

	private static final KeyBinding fovToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.ignotusmod.fov_toggle",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_R,    // Change this to the GLFW code for the desired key
			"category.ignotusmod.fov_toggle"
	));

	private Integer defaultFOV;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientTickEvents.END_CLIENT_TICK.register(this::onPlayerTick);
		ClientTickEvents.END_CLIENT_TICK.register(this::toggleFOV);
	}

	private void toggleFOV(MinecraftClient minecraftClient) {
		GameOptions options = minecraftClient.options;
		if (defaultFOV == null) {
			defaultFOV = options.getFov().getValue();
		}

		if (fovToggleKey.isPressed()) {
			// Set a new FOV value when toggled on (e.g., zoomed in)
			options.getFov().setValue(30); // Adjust the FOV value as needed
			options.smoothCameraEnabled = true;
		} else {
			// Set the default FOV when toggled off (e.g., zoomed out)
			options.getFov().setValue(defaultFOV);
			options.smoothCameraEnabled = false;
		}
	}

	private void onPlayerTick(MinecraftClient minecraftClient) {
		PlayerEntity player = minecraftClient.player;
		// Check if the player is moving and on the ground
		if (player != null && !player.isSneaking()
				&& (player.forwardSpeed != 0.0F || player.sidewaysSpeed != 0.0F)
				&& player.isOnGround()
				&& false)
		{
			// Check the player's position and nearby blocks
			int playerX = (int) player.getX();
			int playerZ = (int) player.getZ();
			int playerFeetY = (int) player.getY() - 1; // The Y-coordinate of the block beneath the player's feet

			// You may want to adjust the offsets below based on the size of your player model
			boolean isNearEdge = !player.getWorld().getBlockState(new BlockPos(playerX, playerFeetY, playerZ)).isSolid()
					|| !player.getWorld().getBlockState(new BlockPos(playerX + 1, playerFeetY, playerZ)).isSolid()
					|| !player.getWorld().getBlockState(new BlockPos(playerX - 1, playerFeetY, playerZ)).isSolid()
					|| !player.getWorld().getBlockState(new BlockPos(playerX, playerFeetY, playerZ + 1)).isSolid()
					|| !player.getWorld().getBlockState(new BlockPos(playerX, playerFeetY, playerZ - 1)).isSolid();

			// Trigger the jump if the player is near the edge
			if (isNearEdge) {
				player.jump();
			}
		}
	}
}
