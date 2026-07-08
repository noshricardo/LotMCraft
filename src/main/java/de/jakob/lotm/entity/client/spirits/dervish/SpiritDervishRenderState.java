package de.jakob.lotm.entity.client.spirits.dervish;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class SpiritDervishRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public long mostSignificantBits;
    public long leastSignificantBits;
}
