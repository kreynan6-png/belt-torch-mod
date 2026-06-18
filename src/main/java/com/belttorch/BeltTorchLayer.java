package com.belttorch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BeltTorchLayer extends RenderLayer<PlayerRenderState, PlayerModel> {

    private final ItemRenderer itemRenderer;

    public BeltTorchLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent,
                          ItemRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       PlayerRenderState renderState,
                       float yRot,
                       float xRot) {

        // Only render for the local player (simplest approach)
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Only render on the main player's render pass
        // renderState.isMainPlayer is true for the local player
        if (!renderState.isMainPlayer) return;

        if (!BeltTorchState.hasTorch()) return;

        ItemStack torch = BeltTorchState.getBeltItem();
        float swingAngle = (float) BeltTorchState.getAngle();

        poseStack.pushPose();

        // Position: left hip (belt area)
        // X: -0.25 = left side, Y: 0.0 = hip level, Z: 0.1 = slightly forward
        poseStack.translate(-0.25f, 0.0f, 0.1f);

        // Apply pendulum swing (rotate around Z axis)
        poseStack.mulPose(Axis.ZP.rotation(swingAngle));

        // Tilt torch slightly so it hangs naturally
        poseStack.mulPose(Axis.XP.rotationDegrees(15.0f));

        // Scale down
        poseStack.scale(0.45f, 0.45f, 0.45f);

        // Render the torch item
        itemRenderer.renderStatic(
            torch,
            ItemDisplayContext.FIXED,
            packedLight,
            0,
            poseStack,
            bufferSource,
            mc.level,
            0
        );

        poseStack.popPose();
    }
}
