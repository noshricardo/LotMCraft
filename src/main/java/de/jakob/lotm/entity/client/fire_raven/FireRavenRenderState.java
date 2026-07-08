package de.jakob.lotm.entity.client.fire_raven;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class FireRavenRenderState extends LivingEntityRenderState {
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public boolean isFlying;
    public float walkLimbSwing;
    public float walkLimbSwingAmount;
}
