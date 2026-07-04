package de.jakob.lotm.util.data;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypes;
import net.minecraft.client.renderer.RenderSetup;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.resources.Identifier;

public class ModRenderTypes extends RenderType {
    
    // Constructor required by RenderType but not used
    public ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, 
                         int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, 
                         Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
    
    // Custom render type for electric bolts with enhanced glow
    public static final RenderType ELECTRIC_BOLT = create("electric_bolt",
        RenderSetup.builder(RenderPipelines.LIGHTNING)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .useLightmap(false)
            .useOverlay(false)
            .createRenderSetup());

    // Alternative electric render type with additive blending for stronger glow
    public static final RenderType ELECTRIC_GLOW = create("electric_glow",
        RenderSetup.builder(RenderPipelines.LIGHTNING)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .useLightmap(false)
            .useOverlay(false)
            .createRenderSetup());

    // Textured electric effect (if you want to use a lightning texture)
    public static RenderType electricTextured(Identifier texture) {
        return create("electric_textured",
            RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT)
                .withTexture("sampler", texture)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .useLightmap(true)
                .useOverlay(true)
                .createRenderSetup());
    }

    // Beam render type for continuous beams (like laser beams)
    public static final RenderType ENERGY_BEAM = create("energy_beam",
        RenderSetup.builder(RenderPipelines.LIGHTNING)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .useLightmap(false)
            .useOverlay(false)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .createRenderSetup());

    // Magical effect render type with special blending
    public static final RenderType MAGICAL_EFFECT = create("magical_effect",
        RenderSetup.builder(RenderPipelines.LIGHTNING)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .useLightmap(false)
            .useOverlay(false)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST) // Render through blocks for magical effects
            .createRenderSetup());
}