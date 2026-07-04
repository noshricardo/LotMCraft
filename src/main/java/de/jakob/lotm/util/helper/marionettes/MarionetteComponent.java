package de.jakob.lotm.util.helper.marionettes;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;

public class MarionetteComponent {
    private boolean isMarionette = false;
    private String controllerUUID = "";
    private boolean followMode = true;
    private boolean shouldAttack = true;

    public MarionetteComponent() {}
    
    public MarionetteComponent(boolean isMarionette, String controllerUUID) {
        this.isMarionette = isMarionette;
        this.controllerUUID = controllerUUID;
    }
    
    // Getters and setters
    public boolean isMarionette() { return isMarionette; }
    public void setMarionette(boolean marionette) { this.isMarionette = marionette; }
    public String getControllerUUID() { return controllerUUID; }
    public void setControllerUUID(String controllerUUID) { this.controllerUUID = controllerUUID; }
    public boolean isFollowMode() { return followMode; }
    public void setFollowMode(boolean followMode) { this.followMode = followMode; }
    public boolean shouldAttack() { return shouldAttack; }
    public void setShouldAttack(boolean shouldAttack) { this.shouldAttack = shouldAttack; }
    
    public static final IAttachmentSerializer<MarionetteComponent> SERIALIZER =
            new IAttachmentSerializer<>() {
                @Override
                public MarionetteComponent read(IAttachmentHolder holder, ValueInput input) {
                    MarionetteComponent component = new MarionetteComponent();
                    component.isMarionette = input.getBooleanOr("isMarionette", false);
                    component.controllerUUID = input.getStringOr("controllerUUID", "");
                    component.followMode = input.getBooleanOr("followMode", false);
                    component.shouldAttack = input.getBooleanOr("shouldAttack", false);
                    return component;
                }

                @Override
                public void write(MarionetteComponent component, ValueOutput output) {
                    output.putBoolean("isMarionette", component.isMarionette);
                    output.putString("controllerUUID", component.controllerUUID);
                    output.putBoolean("followMode", component.followMode);
                    output.putBoolean("shouldAttack", component.shouldAttack);
                }
            };
}
