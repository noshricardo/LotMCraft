package de.jakob.lotm.attachments;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class QuestComponent {

    private HashSet<String> completedQuests = new HashSet<>();
    private HashMap<String, Float> questProgress = new HashMap<>();
    private HashMap<String, Vec3> questLocation = new HashMap<>();
    private HashMap<String, List<ItemStack>> lockedQuestRewards = new HashMap<>();
    private HashMap<String, Float> lockedQuestDigestionRewards = new HashMap<>();

    public QuestComponent() {}

    public HashSet<String> getCompletedQuests() {
        return completedQuests;
    }

    public HashMap<String, Float> getQuestProgress() {
        return questProgress;
    }

    public HashMap<String, Vec3> getQuestLocation() {
        return questLocation;
    }

    public HashMap<String, List<ItemStack>> getLockedQuestRewards() {
        return lockedQuestRewards;
    }

    public HashMap<String, Float> getLockedQuestDigestionRewards() {
        return lockedQuestDigestionRewards;
    }


    public static final IAttachmentSerializer<QuestComponent> SERIALIZER =
            new IAttachmentSerializer<>() {
                @Override
                public QuestComponent read(IAttachmentHolder holder, ValueInput input) {
                    QuestComponent component = new QuestComponent();
                    
                    component.completedQuests.addAll(input.readCollection("CompletedQuests", HashSet::new, in -> in.getStringOr(null, "")));
                    
                    component.questProgress.putAll(input.readMap("QuestProgress", HashMap::new, in -> in.getStringOr(null, ""), in -> in.getFloatOr(null, 0f)));
                    
                    component.questLocation.putAll(input.readMap("QuestLocation", HashMap::new, in -> in.getStringOr(null, ""), in -> {
                        float x = in.getFloatOr("x", 0f);
                        float y = in.getFloatOr("y", 0f);
                        float z = in.getFloatOr("z", 0f);
                        return new Vec3(x, y, z);
                    }));

                    // For ItemStacks, we'll use codecs if supported by ValueInput or just skip for now if too complex
                    // Actually, I'll try to find if ValueInput supports codecs
                    // TODO: Implement ItemStack serialization in Value I/O
                    
                    component.lockedQuestDigestionRewards.putAll(input.readMap("LockedQuestDigestionRewards", HashMap::new, in -> in.getStringOr(null, ""), in -> in.getFloatOr(null, 0f)));

                    return component;
                }

                @Override
                public void write(QuestComponent component, ValueOutput output) {
                    output.putCollection("CompletedQuests", component.completedQuests, (s, out) -> out.putString(null, s));
                    
                    output.putMap("QuestProgress", component.questProgress, (k, out) -> out.putString(null, k), (v, out) -> out.putFloat(null, v));
                    
                    output.putMap("QuestLocation", component.questLocation, (k, out) -> out.putString(null, k), (v, out) -> {
                        out.putFloat("x", (float) v.x);
                        out.putFloat("y", (float) v.y);
                        out.putFloat("z", (float) v.z);
                    });
                    
                    // TODO: Implement ItemStack serialization
                    
                    output.putMap("LockedQuestDigestionRewards", component.lockedQuestDigestionRewards, (k, out) -> out.putString(null, k), (v, out) -> out.putFloat(null, v));
                }
            };
}