package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class CopiedAbilityComponent implements ValueIOSerializable {

    public static final int MAX_ABILITIES = 24;

    public record CopiedAbilityData(String abilityId, String copyType, int remainingUses, String originalOwnerUUID) {

        public void serialize(ValueOutput output) {
            output.putString("AbilityId", abilityId);
            output.putString("CopyType", copyType);
            output.putInt("RemainingUses", remainingUses);
            output.putString("OriginalOwnerUUID", originalOwnerUUID != null ? originalOwnerUUID : "");
        }

        public static CopiedAbilityData deserialize(ValueInput input) {
            String abilityId = input.getStringOr("AbilityId", "");
            String copyType = input.getStringOr("CopyType", "");
            int remainingUses = input.getIntOr("RemainingUses", 0);
            String ownerUUID = input.getStringOr("OriginalOwnerUUID", "");
            return new CopiedAbilityData(abilityId, copyType, remainingUses, ownerUUID.isEmpty() ? null : ownerUUID);
        }

        public CopiedAbilityData withRemainingUses(int uses) {
            return new CopiedAbilityData(abilityId, copyType, uses, originalOwnerUUID);
        }
    }

    private final ArrayList<CopiedAbilityData> abilities = new ArrayList<>();

    public void addAbility(CopiedAbilityData data) {
        if (abilities.size() >= MAX_ABILITIES) {
            abilities.remove(0);
        }
        abilities.add(data);
    }

    public void removeAbility(int index) {
        if (index >= 0 && index < abilities.size()) {
            abilities.remove(index);
        }
    }

    public CopiedAbilityData getAbility(int index) {
        if (index >= 0 && index < abilities.size()) {
            return abilities.get(index);
        }
        return null;
    }

    public ArrayList<CopiedAbilityData> getAbilities() {
        return abilities;
    }

    public int size() {
        return abilities.size();
    }

    public List<String> getAbilityIds() {
        List<String> ids = new ArrayList<>();
        for (CopiedAbilityData data : abilities) {
            ids.add(data.abilityId());
        }
        return ids;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putCollection("CopiedAbilities", abilities, (data, out) -> data.serialize(out));
    }

    @Override
    public void deserialize(ValueInput input) {
        abilities.clear();
        abilities.addAll(input.readCollection("CopiedAbilities", ArrayList::new, CopiedAbilityData::deserialize));
    }
}
