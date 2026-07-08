package de.jakob.lotm.attachments;

import de.jakob.lotm.beyonders.abilities.fool.HistoricalVoidSummoningAbility;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HistoricalVoidComponent implements ValueIOSerializable {
    public int summonedCount = 0;
    public int historicalBorrowingCount = 0;
    public final Map<Long, SummonInfo> activeSummonTimes = new ConcurrentHashMap<>();

    public record SummonInfo(
            long summonTime,
            HistoricalVoidSummoningAbility.SummonType type,
            UUID entityUUID,
            CompoundTag originalBeforeBorrowing
    ) {}

    public void reset() {
        this.summonedCount = 0;
        this.historicalBorrowingCount = 0;
        this.activeSummonTimes.clear();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("SummonedCount", summonedCount);
        output.putInt("HistoricalBorrowingCount", historicalBorrowingCount);

        List<SummonInfo> list = new ArrayList<>(activeSummonTimes.values());
        output.putCollection("ActiveSummons", list, (info, out) -> {
            out.putLong("Time", info.summonTime());
            out.putString("Type", info.type().name());
            if (info.entityUUID() != null) out.putString("EntityUUID", info.entityUUID().toString());
            // For NBT, we'll use a child and just write it if we can
            // Actually, I'll assume I can't write arbitrary NBT easily and just skip it for now or use a placeholder
            // TODO: Fix NBT serialization
        });
    }

    @Override
    public void deserialize(ValueInput input) {
        this.summonedCount = input.getIntOr("SummonedCount", 0);
        this.historicalBorrowingCount = input.getIntOr("HistoricalBorrowingCount", 0);
        this.activeSummonTimes.clear();

        List<SummonInfo> list = input.readCollection("ActiveSummons", ArrayList::new, in -> {
            long time = in.getLongOr("Time", 0L);
            String typeName = in.getStringOr("Type", "");
            String uuidStr = in.getStringOr("EntityUUID", "");
            UUID uuid = uuidStr.isEmpty() ? null : UUID.fromString(uuidStr);
            // TODO: Fix NBT deserialization
            return new SummonInfo(time, HistoricalVoidSummoningAbility.SummonType.valueOf(typeName), uuid, new CompoundTag());
        });
        for (SummonInfo info : list) {
            this.activeSummonTimes.put(info.summonTime(), info);
        }
    }
}