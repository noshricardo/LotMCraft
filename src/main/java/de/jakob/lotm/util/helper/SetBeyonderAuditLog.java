package de.jakob.lotm.util.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.jakob.lotm.LOTMCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SetBeyonderAuditLog extends SavedData {

    public static final String NBT_KEY = "setbeyonder_audit_log";
    private static final int MAX_ENTRIES = 500;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public record AuditEntry(long timestamp, String executorName, String targetName, String pathway, int sequence, String fullCommand) {
        public static final Codec<AuditEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("timestamp").forGetter(AuditEntry::timestamp),
                Codec.STRING.fieldOf("executor").forGetter(AuditEntry::executorName),
                Codec.STRING.fieldOf("target").forGetter(AuditEntry::targetName),
                Codec.STRING.fieldOf("pathway").forGetter(AuditEntry::pathway),
                Codec.INT.fieldOf("sequence").forGetter(AuditEntry::sequence),
                Codec.STRING.fieldOf("command").forGetter(AuditEntry::fullCommand)
        ).apply(instance, AuditEntry::new));

        public String format() {
            String time = FORMATTER.format(Instant.ofEpochSecond(timestamp));
            return "[" + time + "] " + executorName + " -> " + targetName
                    + " | " + pathway + " seq " + sequence
                    + " | /" + fullCommand;
        }
    }

    public static final Codec<SetBeyonderAuditLog> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AuditEntry.CODEC.listOf().fieldOf("entries").forGetter(log -> new ArrayList<>(log.entries))
    ).apply(instance, SetBeyonderAuditLog::new));

    public static final SavedDataType<SetBeyonderAuditLog> TYPE = new SavedDataType<>(
            NBT_KEY,
            SetBeyonderAuditLog::new,
            CODEC,
            null
    );

    private final Deque<AuditEntry> entries = new ArrayDeque<>();

    public SetBeyonderAuditLog() {
        super();
    }

    public SetBeyonderAuditLog(List<AuditEntry> entries) {
        this();
        this.entries.addAll(entries);
    }

    public void addEntry(String executorName, String targetName, String pathway, int sequence, String fullCommand) {
        AuditEntry entry = new AuditEntry(
                Instant.now().getEpochSecond(),
                executorName,
                targetName,
                pathway,
                sequence,
                fullCommand
        );
        entries.addLast(entry);
        while (entries.size() > MAX_ENTRIES) {
            entries.removeFirst();
        }
        setDirty();
        LOTMCraft.LOGGER.info("[SetBeyonder Audit] {}", entry.format());
    }

    /** Returns up to {@code limit} most recent entries, newest first. */
    public List<AuditEntry> getRecent(int limit) {
        List<AuditEntry> list = new ArrayList<>(entries);
        int start = Math.max(0, list.size() - limit);
        List<AuditEntry> result = new ArrayList<>(list.subList(start, list.size()));
        java.util.Collections.reverse(result);
        return result;
    }

    public int totalEntries() {
        return entries.size();
    }


    public static SetBeyonderAuditLog get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
