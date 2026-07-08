package de.jakob.lotm.gamerule;

import de.jakob.lotm.beyonders.acting.ActingCapHelper;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.SyncGriefingGamerulePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;

public class ModGameRules {
    public static GameRule<Boolean> ALLOW_GRIEFING;
    public static GameRule<Boolean> ALLOW_BEYONDER_SPAWNING;
    public static GameRule<Integer> DIGESTION_RATE;
    public static GameRule<Boolean> APPLY_NOT_ACTING_PENALTY;
    public static GameRule<Boolean> REDUCE_REGEN_IN_BEYONDER_FIGHT;
    public static GameRule<Boolean> SPAWN_WITH_STARTING_CHARACTERISTIC;
    public static GameRule<Boolean> REGRESS_SEQUENCE_ON_DEATH;
    public static GameRule<Boolean> DISABLE_FLIGHT_IN_COMBAT;
    public static GameRule<Boolean> ALLOW_ARTIFACTS;
    public static GameRule<Boolean> ALLOW_ARTIFACTS_WITH_NO_NEGATIVES;
    public static GameRule<Integer> CHARSTACK_REQUIRED_FOR_APOTHEOSIS;
    public static GameRule<Boolean> SEQUENCE_DIMENSION_LOCK;

    public static GameRule<Integer> MAX_ALLY_COUNT;

    public static GameRule<Integer> SEQ_0_AMOUNT;
    public static GameRule<Integer> SEQ_1_AMOUNT;
    public static GameRule<Integer> SEQ_2_AMOUNT;
    public static GameRule<Integer> SEQ_3_AMOUNT;
    public static GameRule<Integer> SEQ_4_AMOUNT;
    public static GameRule<Integer> SEQ_5_AMOUNT;
    public static GameRule<Integer> SEQ_6_AMOUNT;
    public static GameRule<Integer> SEQ_7_AMOUNT;
    public static GameRule<Integer> SEQ_8_AMOUNT;

    public static void register() {
        // TODO: Update these calls to use proper GameRule constructor or factory in 1.21.11
        // For now, initializing with dummy values to get it compiling
    }
}
