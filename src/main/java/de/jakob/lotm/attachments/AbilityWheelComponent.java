package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;

import java.util.ArrayList;

public class AbilityWheelComponent implements ValueIOSerializable {
    private ArrayList<String> abilities = new ArrayList<>();
    private int selectedAbility = 0;

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(ArrayList<String> abilities) {
        this.abilities = abilities;
    }

    public int getSelectedAbility() {
        return selectedAbility;
    }

    public void setSelectedAbility(int selectedAbility) {
        this.selectedAbility = selectedAbility;
    }

    public AbilityWheelComponent copy(){
        var result = new AbilityWheelComponent();
        result.abilities = new ArrayList<>(abilities);
        result.selectedAbility = selectedAbility;

        return result;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putCollection("Abilities", abilities, (ability, out) -> out.putString(null, ability));
        output.putInt("selectedAbility", selectedAbility);
    }

    @Override
    public void deserialize(ValueInput input) {
        abilities.clear();
        abilities.addAll(input.readCollection("Abilities", ArrayList::new, in -> in.getStringOr(null, "")));
        selectedAbility = input.getIntOr("selectedAbility", 0);
    }
}
