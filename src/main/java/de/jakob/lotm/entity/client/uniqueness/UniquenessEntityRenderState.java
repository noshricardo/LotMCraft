package de.jakob.lotm.entity.client.uniqueness;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.item.ItemStack;

public class UniquenessEntityRenderState extends EntityRenderState {
    public ItemStack itemStack = ItemStack.EMPTY;
    public float tick;
}
