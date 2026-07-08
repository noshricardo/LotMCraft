package de.jakob.lotm.entity.client.spirits.blue_wizard;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class SpiritBlueWizardRenderState extends LivingEntityRenderState {
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public boolean isWalking;
}
