package de.jakob.lotm.entity.client.spirits.bubbles;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class SpiritBubblesRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public float headYaw;
    public float headPitch;
}
