package de.jakob.lotm.util.helper.subordinates;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class SubordinateComponent {
    private boolean isSubordinate = false;
    private String controllerUUID = "";
    private boolean followMode = true;
    private boolean shouldAttack = true;

    public SubordinateComponent() {}

    public SubordinateComponent(boolean isMarionette, String controllerUUID) {
        this.isSubordinate = isMarionette;
        this.controllerUUID = controllerUUID;
    }
    
    // Getters and setters
    public boolean isSubordinate() { return isSubordinate; }
    public void setSubordinate(boolean subordinate) { this.isSubordinate = subordinate; }
    public String getControllerUUID() { return controllerUUID; }
    public void setControllerUUID(String controllerUUID) { this.controllerUUID = controllerUUID; }
    public boolean isFollowMode() { return followMode; }
    public void setFollowMode(boolean followMode) { this.followMode = followMode; }
    public boolean shouldAttack() { return shouldAttack; }
    public void setShouldAttack(boolean shouldAttack) { this.shouldAttack = shouldAttack; }
    
    public static final IAttachmentSerializer<SubordinateComponent> SERIALIZER =
            new IAttachmentSerializer<>() {
                @Override
                public SubordinateComponent read(IAttachmentHolder holder, ValueInput input) {
                    SubordinateComponent component = new SubordinateComponent();
                    component.isSubordinate = input.getBooleanOr("isSubordinate", false);
                    component.controllerUUID = input.getStringOr("controllerUUID", "");
                    component.followMode = input.getBooleanOr("followMode", false);
                    component.shouldAttack = input.getBooleanOr("shouldAttack", false);
                    return component;
                }

                @Override
                public void write(SubordinateComponent component, ValueOutput output) {
                    output.putBoolean("isSubordinate", component.isSubordinate);
                    output.putString("controllerUUID", component.controllerUUID);
                    output.putBoolean("followMode", component.followMode);
                    output.putBoolean("shouldAttack", component.shouldAttack);
                }
            };
}
