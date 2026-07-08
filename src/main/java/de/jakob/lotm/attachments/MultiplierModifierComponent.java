package de.jakob.lotm.attachments;

import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.DisableAbilityUsageForTimePacket;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiplierModifierComponent implements ValueIOSerializable {

    public HashMap<String, MultiplierModifier> modifiers = new HashMap<>();

    public void addMultiplier(String cause, float multiplier) {
        modifiers.computeIfAbsent(cause, k -> new MultiplierModifier(multiplier, 0));
        modifiers.put(cause, new MultiplierModifier(multiplier, modifiers.get(cause).amount + 1));
    }

    public void removeMultiplier(String cause) {
        if(modifiers.containsKey(cause)) {
            MultiplierModifier modifier = modifiers.get(cause);
            if(modifier.amount <= 1) {
                modifiers.remove(cause);
            } else {
                modifiers.put(cause, new MultiplierModifier(modifier.multiplier, modifier.amount - 1));
            }
        }
    }

    public void addMultiplierForTime(String cause, float multiplier, int ticks) {
        addMultiplier(cause, multiplier);

        ServerScheduler.scheduleDelayed(ticks, () -> removeMultiplier(cause));
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putMap("modifiers", modifiers, (k, out) -> out.putString(null, k), (v, out) -> {
            out.putFloat("multiplier", v.multiplier);
            out.putInt("amount", v.amount);
        });
    }

    @Override
    public void deserialize(ValueInput input) {
        modifiers.clear();
        modifiers.putAll(input.readMap("modifiers", HashMap::new, in -> in.getStringOr(null, ""), in -> {
            float multiplier = in.getFloatOr("multiplier", 1.0f);
            int amount = in.getIntOr("amount", 0);
            return new MultiplierModifier(multiplier, amount);
        }));
    }

    public record MultiplierModifier(float multiplier, int amount) {
    }
}
