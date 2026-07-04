package de.jakob.lotm.entity.client.beyonder_npc;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.entity.custom.BeyonderNPCEntity;
import de.jakob.lotm.util.shapeShifting.PlayerSkinData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class BeyonderNPCRenderer extends MobRenderer<BeyonderNPCEntity, BeyonderNPCRenderState, PlayerModel<BeyonderNPCRenderState>> {
    private final PlayerModel<BeyonderNPCRenderState> wideModel;
    private final PlayerModel<BeyonderNPCRenderState> slimModel;

    public BeyonderNPCRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.wideModel = this.model;
        this.slimModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        // layers registration...
    }

    @Override
    public BeyonderNPCRenderState createRenderState() {
        return new BeyonderNPCRenderState();
    }

    @Override
    public void extractRenderState(BeyonderNPCEntity entity, BeyonderNPCRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSlim = entity.getTargetPlayerUUID().map(PlayerSkinData::isSlimModel).orElse(false);
        state.skinTexture = getSkinTexture(entity);
    }

    private Identifier getSkinTexture(BeyonderNPCEntity entity) {
        if (entity.getTargetPlayerUUID().isPresent()) {
            Identifier cached = PlayerSkinData.getSkinTexture(entity.getTargetPlayerUUID().get());
            if (cached != null) {
                return cached;
            }
            PlayerSkinData.fetchAndCacheSkin(entity.getTargetPlayerUUID().get());
            return DefaultPlayerSkin.get(entity.getTargetPlayerUUID().get()).texture();
        } else {
            return entity.getSkinTexture();
        }
    }

    public Identifier getTextureLocation(BeyonderNPCRenderState state) {
        return state.skinTexture;
    }
}