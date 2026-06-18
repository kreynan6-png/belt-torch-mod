package com.belttorch;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BeltTorchState {

    // The torch currently equipped on belt
    private static ItemStack beltItem = ItemStack.EMPTY;

    // Pendulum physics: angle in radians, angular velocity
    private static double angle = 0.0;
    private static double velocity = 0.0;

    private static final double GRAVITY = 0.12;
    private static final double DAMPING = 0.88;

    public static boolean isTorchItem(ItemStack stack) {
        return stack.is(Items.TORCH)
            || stack.is(Items.SOUL_TORCH)
            || stack.is(Items.LANTERN)
            || stack.is(Items.SOUL_LANTERN)
            || stack.is(Items.GLOWSTONE)
            || stack.is(Items.SHROOMLIGHT)
            || stack.is(Items.SEA_LANTERN);
    }

    public static void toggle(Minecraft client) {
        if (client.player == null) return;

        if (!beltItem.isEmpty()) {
            // Detach - give back to player
            if (!client.player.addItem(beltItem)) {
                client.player.drop(beltItem, false);
            }
            beltItem = ItemStack.EMPTY;
            angle = 0.0;
            velocity = 0.0;
            client.player.displayClientMessage(
                Component.literal("§c[Belt] Torch removed"), true);
        } else {
            // Try to attach from hands
            ItemStack main = client.player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack off  = client.player.getItemInHand(InteractionHand.OFF_HAND);

            ItemStack toEquip = null;
            InteractionHand usedHand = null;

            if (isTorchItem(main)) {
                toEquip = main.copyWithCount(1);
                main.shrink(1);
                usedHand = InteractionHand.MAIN_HAND;
            } else if (isTorchItem(off)) {
                toEquip = off.copyWithCount(1);
                off.shrink(1);
                usedHand = InteractionHand.OFF_HAND;
            }

            if (toEquip != null) {
                beltItem = toEquip;
                angle = 0.0;
                velocity = 0.0;
                client.player.displayClientMessage(
                    Component.literal("§a[Belt] Torch equipped - hold shield freely!"), true);
            } else {
                client.player.displayClientMessage(
                    Component.literal("§e[Belt] Hold a torch or lantern first!"), true);
            }
        }
    }

    public static void tick(Minecraft client) {
        if (beltItem.isEmpty() || client.player == null) return;

        // Pendulum physics
        velocity -= GRAVITY * Math.sin(angle);
        velocity *= DAMPING;
        angle += velocity;

        // Add force based on player horizontal movement
        double dx = client.player.getDeltaMovement().x;
        double dz = client.player.getDeltaMovement().z;
        double speed = Math.sqrt(dx * dx + dz * dz);
        if (speed > 0.01) {
            velocity += speed * 0.25;
        }

        // Clamp angle so torch doesn't spin forever
        if (angle > Math.PI / 3) { angle = Math.PI / 3; velocity *= -0.5; }
        if (angle < -Math.PI / 3) { angle = -Math.PI / 3; velocity *= -0.5; }
    }

    public static ItemStack getBeltItem() { return beltItem; }
    public static double getAngle() { return angle; }
    public static boolean hasTorch() { return !beltItem.isEmpty(); }
    public static int getLightLevel() {
        if (beltItem.isEmpty()) return 0;
        if (beltItem.is(Items.TORCH) || beltItem.is(Items.LANTERN)) return 14;
        if (beltItem.is(Items.SOUL_TORCH) || beltItem.is(Items.SOUL_LANTERN)) return 10;
        if (beltItem.is(Items.GLOWSTONE) || beltItem.is(Items.SEA_LANTERN)) return 15;
        if (beltItem.is(Items.SHROOMLIGHT)) return 15;
        return 12;
    }
}
