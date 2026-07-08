package de.jakob.lotm.entity.client.avatar;

import com.mojang.authlib.GameProfile;
import de.jakob.lotm.entity.custom.AvatarEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class ErrorAvatarRenderer extends MobRenderer<AvatarEntity, AvatarRenderState, PlayerModel> {
    // Cache for player skins to avoid repeated lookups
    private static final Map<UUID, Identifier> SKIN_CACHE = new ConcurrentHashMap<>();

    public ErrorAvatarRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new AvatarRenderState();
    }

    @Override
    public void extractRenderState(AvatarEntity entity, AvatarRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        UUID ownerUUID = entity.getOriginalOwner();
        if (ownerUUID == null) {
            state.skin = DefaultPlayerSkin.get(java.util.UUID.randomUUID());
        } else {
            state.skin = Minecraft.getInstance().getSkinManager().createLookup(new GameProfile(ownerUUID, null), true).get();
        }
    }

    @Override
    public Identifier getTextureLocation(AvatarRenderState state) {
        return state.skin.body().texturePath();
    }

    /**
     * Clears the skin cache. Can be called when needed (e.g., on resource reload)
     */
    public static void clearSkinCache() {
        SKIN_CACHE.clear();
    }
}