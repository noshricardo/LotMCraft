package de.jakob.lotm.attachments;

import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.UUID;

public class ParasitationComponent {

    private boolean isParasited = false;
    private UUID parasiteUUID = null;

    public ParasitationComponent() {
    }

    public boolean isParasited() {
        return isParasited;
    }

    public void setParasited(boolean parasited) {
        isParasited = parasited;
    }

    public UUID getParasiteUUID() {
        return parasiteUUID;
    }

    public void setParasiteUUID(UUID parasiteUUID) {
        this.parasiteUUID = parasiteUUID;
    }

    public static final IAttachmentSerializer<ParasitationComponent> SERIALIZER =
            new IAttachmentSerializer<>() {
                @Override
                public ParasitationComponent read(IAttachmentHolder holder, ValueInput input) {
                    ParasitationComponent component = new ParasitationComponent();
                    String uuidStr = input.getStringOr("hostUUID", "");
                    component.parasiteUUID = uuidStr.isEmpty() ? null : UUID.fromString(uuidStr);
                    component.isParasited = input.getBooleanOr("isParasited", false);
                    return component;
                }

                @Override
                public void write(ParasitationComponent component, ValueOutput output) {
                    if (component.parasiteUUID != null) {
                        output.putString("hostUUID", component.parasiteUUID.toString());
                    }
                    output.putBoolean("isParasited", component.isParasited);
                }
            };
}
