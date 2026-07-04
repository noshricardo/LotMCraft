package de.jakob.lotm.attachments;

import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class DisabledFlightComponent {

    private int cooldownTicks = 0;

    public DisabledFlightComponent() {
    }


    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public void setCooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
    }


    public static final IAttachmentSerializer<DisabledFlightComponent> SERIALIZER =
            new IAttachmentSerializer<>() {
                @Override
                public DisabledFlightComponent read(IAttachmentHolder holder, ValueInput input) {
                    DisabledFlightComponent component = new DisabledFlightComponent();
                    component.cooldownTicks = input.getIntOr("cooldownTicks", 0);
                    return component;
                }

                @Override
                public void write(DisabledFlightComponent component, ValueOutput output) {
                    output.putInt("cooldownTicks", component.cooldownTicks);
                }
            };
}
