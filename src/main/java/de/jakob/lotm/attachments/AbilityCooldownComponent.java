package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;

import java.util.HashMap;
import java.util.Map;

public class AbilityCooldownComponent implements ValueIOSerializable {
    private final Map<String, Integer> cooldowns = new HashMap<>();
    
    public void setCooldown(String abilityId, int ticks) {
        cooldowns.put(abilityId, ticks);
    }
    
    public boolean isOnCooldown(String abilityId) {
        return cooldowns.getOrDefault(abilityId, 0) > 0;
    }
    
    public int getRemainingCooldown(String abilityId) {
        return cooldowns.getOrDefault(abilityId, 0);
    }

    public void removeAllCooldowns() {
        cooldowns.clear();
    }

    public void tick() {
        cooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
        cooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putMap("cooldowns", cooldowns, (k, out) -> out.putString(null, k), (v, out) -> out.putInt(null, v));
    }

    @Override
    public void deserialize(ValueInput input) {
        cooldowns.clear();
        cooldowns.putAll(input.readMap("cooldowns", HashMap::new, in -> in.getStringOr(null, ""), in -> in.getIntOr(null, 0)));
    }
}