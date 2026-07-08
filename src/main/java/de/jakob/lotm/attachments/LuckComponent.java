package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.UnknownNullability;

public class LuckComponent implements ValueIOSerializable {

    private int luck = 0;

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
        if(this.luck > 3000) {
            this.luck = 3000;
        }
        if(this.luck < -6360) {
            this.luck = -6360;
        }
    }

    public void addLuck(int amount) {
        this.luck += amount;
        if(luck > 3000) {
            luck = 3000;
        }
        if(luck < -6360) {
            luck = -6360;
        }
    }

    public void addLuckWithMax(int amount, int max) {
        if (amount >= 0 && this.luck + amount > max) {
            this.luck = max;
        } else {
            this.luck += amount;
        }
    }

    public void addLuckWithMin(int amount, int min) {
        if (amount < 0 && this.luck + amount < min) {
            this.luck = -min;
        } else {
            this.luck += amount;
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("luck", luck);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.luck = input.getIntOr("luck", 0);
    }
}
