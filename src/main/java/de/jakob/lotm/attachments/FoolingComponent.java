package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.common.util.ValueInput;
import net.neoforged.neoforge.common.util.ValueOutput;
import org.jetbrains.annotations.UnknownNullability;

public class FoolingComponent implements ValueIOSerializable {

    private int ticksRemaining = 0;
    private int stunTicksRemaining = 0;

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void setTicksRemaining(int ticks) {
        this.ticksRemaining = Math.max(0, ticks);
    }

    public boolean isFooled() {
        return ticksRemaining > 0;
    }

    public boolean isStunned() {
        return stunTicksRemaining > 0;
    }

    public void applyStun(int ticks) {
        this.stunTicksRemaining = Math.max(this.stunTicksRemaining, ticks);
    }

    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
        if (stunTicksRemaining > 0) stunTicksRemaining--;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("fooling_ticks", ticksRemaining);
        output.putInt("stun_ticks", stunTicksRemaining);
    }

    @Override
    public void deserialize(ValueInput input) {
        ticksRemaining = input.getIntOr("fooling_ticks", 0);
        stunTicksRemaining = input.getIntOr("stun_ticks", 0);
    }
}
