package com.belttorch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class BeltTorchMod implements ClientModInitializer {

    public static final String MOD_ID = "belttorch";
    public static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        // Register keybind - default B
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.belttorch.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.belttorch"
        ));

        // Tick event - handle keybind press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                BeltTorchState.toggle(client);
            }
            BeltTorchState.tick(client);
        });

        // Register render layer on player
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
            (entityType, entityRenderer, registrationHelper, context) -> {
                if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                    registrationHelper.register(
                        new BeltTorchLayer(playerRenderer, context.getItemRenderer())
                    );
                }
            }
        );
    }
}
