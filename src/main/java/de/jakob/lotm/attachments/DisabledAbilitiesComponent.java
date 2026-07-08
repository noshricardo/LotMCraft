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

public class DisabledAbilitiesComponent implements ValueIOSerializable {

    private final HashMap<String, Integer> hasAllAbilitiesDisabled = new HashMap<>();
    private final HashMap<String, List<DisabledAbility>> disabledAbilities = new HashMap<>();

    public List<DisabledAbility> getAllDisabledAbilities() {
        List<DisabledAbility> all = new ArrayList<>();
        for (List<DisabledAbility> list : disabledAbilities.values()) {
            all.addAll(list);
        }
        return all;
    }

    public void disableAbilityUsage(String cause) {
        hasAllAbilitiesDisabled.put(cause, 1);
    }

    public void enableAbilityUsage(String cause) {
        hasAllAbilitiesDisabled.remove(cause);
    }

    public void disableAbilityUsageForTime(String cause, int ticks, LivingEntity entity) {
        hasAllAbilitiesDisabled.put(cause, hasAllAbilitiesDisabled.getOrDefault(cause, 0) + 1);

        ServerScheduler.scheduleDelayed(ticks, () -> {
            hasAllAbilitiesDisabled.put(cause, hasAllAbilitiesDisabled.getOrDefault(cause, 1) - 1);
            if (hasAllAbilitiesDisabled.getOrDefault(cause, 0) <= 0) {
                hasAllAbilitiesDisabled.remove(cause);
            }
        });

        if(entity instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(player, new DisableAbilityUsageForTimePacket(entity.getId(), cause, ticks));
        }
    }

    public void disableSpecificAbility(String ability, String cause) {
        disabledAbilities.computeIfAbsent(cause, k -> new ArrayList<>()).add(new DisabledAbility(ability, 1));
    }

    public void enableSpecificAbility(String ability, String cause) {
        List<DisabledAbility> abilities = disabledAbilities.get(cause);
        if (abilities != null) {
            abilities.removeIf(da -> da.ability.equals(ability));
            if (abilities.isEmpty()) {
                disabledAbilities.remove(cause);
            }
        }
    }

    public void disableSpecificAbilityForTime(String ability, String cause, int ticks) {
        disabledAbilities.computeIfAbsent(cause, k -> new ArrayList<>());

        if(disabledAbilities.get(cause).stream().noneMatch(da -> da.ability.equals(ability))) {
            disabledAbilities.get(cause).add(new DisabledAbility(ability, 1));
        } else {
            DisabledAbility da = disabledAbilities.get(cause).stream().filter(d -> d.ability.equals(ability)).findFirst().orElseThrow();
            disabledAbilities.get(cause).remove(da);
            disabledAbilities.get(cause).add(new DisabledAbility(ability, da.amountDisabled + 1));
        }

        ServerScheduler.scheduleDelayed(ticks, () -> {
            List<DisabledAbility> abilities = disabledAbilities.get(cause);
            if (abilities != null) {
                DisabledAbility da = abilities.stream().filter(d -> d.ability.equals(ability)).findFirst().orElseThrow();
                abilities.remove(da);
                if (da.amountDisabled > 1) {
                    abilities.add(new DisabledAbility(ability, da.amountDisabled - 1));
                }
                if (abilities.isEmpty()) {
                    disabledAbilities.remove(cause);
                }
            }
        });
    }

    public boolean isAbilityUsageDisabled() {
        return !hasAllAbilitiesDisabled.isEmpty();
    }

    public boolean isSpecificAbilityDisabled(String ability) {
        return disabledAbilities.values().stream().anyMatch(list -> list.stream().anyMatch(da -> da.ability.equals(ability)));
    }

    public void enableAllAbilities() {
        hasAllAbilitiesDisabled.clear();
        disabledAbilities.clear();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putMap("allDisabled", hasAllAbilitiesDisabled, (k, out) -> out.putString(null, k), (v, out) -> out.putInt(null, v));

        List<Triple<String, String, Integer>> flatList = new ArrayList<>();
        disabledAbilities.forEach((cause, abilities) -> {
            abilities.forEach(da -> flatList.add(new Triple<>(cause, da.ability, da.amountDisabled)));
        });

        output.putCollection("specificDisabled", flatList, (t, out) -> {
            out.putString("cause", t.left());
            out.putString("ability", t.middle());
            out.putInt("amount", t.right());
        });
    }

    @Override
    public void deserialize(ValueInput input) {
        hasAllAbilitiesDisabled.clear();
        hasAllAbilitiesDisabled.putAll(input.readMap("allDisabled", HashMap::new, in -> in.getStringOr(null, ""), in -> in.getIntOr(null, 0)));

        disabledAbilities.clear();
        input.readCollection("specificDisabled", ArrayList::new, in -> {
            String cause = in.getStringOr("cause", "");
            String ability = in.getStringOr("ability", "");
            int amount = in.getIntOr("amount", 0);
            disabledAbilities.computeIfAbsent(cause, k -> new ArrayList<>()).add(new DisabledAbility(ability, amount));
            return null;
        });
    }

    private record Triple<L, M, R>(L left, M middle, R right) {}

    public record DisabledAbility(String ability, int amountDisabled) {

    }
}
