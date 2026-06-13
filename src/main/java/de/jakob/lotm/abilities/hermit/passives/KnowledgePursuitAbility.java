package de.jakob.lotm.abilities.hermit.passives;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityItem;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toServer.KnowledgePursuitSeenPacket;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.beyonderMap.HonorificName;
import de.jakob.lotm.util.beyonderMap.StoredData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3f;

import java.util.*;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID, value = Dist.CLIENT)
public class KnowledgePursuitAbility extends PassiveAbilityItem {

    public KnowledgePursuitAbility(Properties properties) {
        super(properties);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 9));
    }

    private record ClusterEntry(Vec3 pos, int birthTick) {}

    private static final int CLUSTER_SIZE = 12;
    private static final double CLUSTER_SPREAD = 0.7;
    private static final double SCATTER_RADIUS = 40.0;
    private static final double SCATTER_VERTICAL = 20.0;
    private static final int SPAWN_INTERVAL_TICKS = 20 * 60;
    private static final int CLUSTER_LIFETIME_TICKS = 20 * 180;
    private static final int MAX_CLUSTERS = 3;
    private static final double LOOK_AT_THRESHOLD = 3.0;
    private static final int EFFECT_SEND_COOLDOWN = 20 * 3;

    private static final Map<UUID, List<ClusterEntry>> activeClusters = new HashMap<>();
    private static final Map<UUID, Integer> spawnTick = new HashMap<>();
    private static final Map<UUID, Integer> effectCooldown = new HashMap<>();
    private static int globalTick = 0;

    @SubscribeEvent
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide)
            return;
        if (Minecraft.getInstance().player == null)
            return;
        if (!Minecraft.getInstance().player.getUUID().equals(player.getUUID()))
            return;
        if (!isHermit(player))
            return;

        globalTick++;

        UUID uuid = player.getUUID();
        activeClusters.putIfAbsent(uuid, new ArrayList<>());
        spawnTick.putIfAbsent(uuid, 0);
        effectCooldown.putIfAbsent(uuid, 0);

        List<ClusterEntry> clusters = activeClusters.get(uuid);

        // Remove expired clusters
        clusters.removeIf(e -> (globalTick - e.birthTick()) >= CLUSTER_LIFETIME_TICKS);

        // Render surviving clusters and check if player is looking at one
        RandomSource rng = player.getRandom();
        boolean lookingAtCluster = false;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (ClusterEntry entry : clusters) {
            renderCluster(level, entry.pos(), rng);

            if (!lookingAtCluster) {
                Vec3 toCluster = entry.pos().subtract(eyePos);
                double dist = toCluster.length();
                if (dist <= 30.0) {
                    double dot = toCluster.normalize().dot(lookVec);
                    double perpendicularDist = Math.sqrt(
                            Math.max(0, dist * dist - (dot * dist) * (dot * dist)));
                    if (dot > 0 && perpendicularDist < LOOK_AT_THRESHOLD) {
                        if (hasLineOfSight(level, player, eyePos, entry.pos())) {
                            lookingAtCluster = true;
                        }
                    }
                }
            }
        }

        // Send packet to server if player is looking at a cluster
        int cd = effectCooldown.get(uuid);
        if (lookingAtCluster && cd <= 0) {
            PacketHandler.sendToServer(new KnowledgePursuitSeenPacket());
            effectCooldown.put(uuid, EFFECT_SEND_COOLDOWN);
        } else if (cd > 0) {
            effectCooldown.put(uuid, cd - 1);
        }

        int ticker = spawnTick.get(uuid) + 1;
        spawnTick.put(uuid, ticker);

        if (ticker < SPAWN_INTERVAL_TICKS)
            return;
        spawnTick.put(uuid, 0);

        if (clusters.size() >= MAX_CLUSTERS)
            return;

        Vec3 pos = player.position();
        double x = pos.x + (rng.nextDouble() * 2 - 1) * SCATTER_RADIUS;
        double z = pos.z + (rng.nextDouble() * 2 - 1) * SCATTER_RADIUS;
        double y = rng.nextBoolean()
                ? pos.y + 8 + rng.nextDouble() * SCATTER_VERTICAL
                : pos.y + 1 + rng.nextDouble() * 3;

        Vec3 clusterCenter = new Vec3(x, y, z);

        if (hasLineOfSight(level, player, player.getEyePosition(), clusterCenter)) {
            clusters.add(new ClusterEntry(clusterCenter, globalTick));
        }
    }


    private static void renderCluster(Level level, Vec3 center, RandomSource rng) {
        for (int i = 0; i < CLUSTER_SIZE; i++) {
            double ox = (rng.nextDouble() * 2 - 1) * CLUSTER_SPREAD;
            double oy = (rng.nextDouble() * 2 - 1) * CLUSTER_SPREAD;
            double oz = (rng.nextDouble() * 2 - 1) * CLUSTER_SPREAD;

            level.addParticle(
                    new DustParticleOptions(new Vector3f(
                            rng.nextFloat(), rng.nextFloat(), rng.nextFloat()), 1.0f),
                    center.x + ox, center.y + oy, center.z + oz,
                    0, 0, 0);

            level.addParticle(ParticleTypes.ENCHANT,
                    center.x + ox * 0.5, center.y + oy * 0.5, center.z + oz * 0.5,
                    0, 0, 0);
        }
    }

    private static boolean hasLineOfSight(Level level, Player player, Vec3 from, Vec3 to) {
        BlockHitResult result = level.clip(new ClipContext(
                from, to,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                player
        ));
        if (result.getType() == HitResult.Type.MISS)
            return true;
        return result.getLocation().distanceTo(to) < 1.5;
    }

    // Cluster interaction with InfoAuthorityAbility

    /** Removes the oldest cluster for a player. Called client-side by RemoveKnowledgeClusterPacket. */
    public static void consumeOldestCluster(UUID playerUUID) {
        List<ClusterEntry> clusters = activeClusters.get(playerUUID);
        if (clusters != null && !clusters.isEmpty())
            clusters.remove(0);
    }

    /** Returns true if the player has at least one active cluster. Called client-side by RequestKnowledgeClusterPacket. */
    public static boolean hasActiveClusters(UUID playerUUID) {
        List<ClusterEntry> clusters = activeClusters.get(playerUUID);
        return clusters != null && !clusters.isEmpty();
    }

    // Negative-effect pool
    private static final MobEffectInstance NAUSEA =
            new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0, false, true, true);

    private static final List<MobEffectInstance> EXTRA_EFFECTS = List.of(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 10, 1, false, true, true),
            new MobEffectInstance(MobEffects.WEAKNESS,          20 * 10, 0, false, true, true),
            new MobEffectInstance(MobEffects.POISON,            20 * 5,  0, false, true, true),
            new MobEffectInstance(MobEffects.BLINDNESS,         20 * 3,  0, false, true, true),
            new MobEffectInstance(MobEffects.HUNGER,            20 * 8,  0, false, true, true)
    );

    private float negativeEffectChance(int sequence) {
        return Math.max(0.10f, 1.0f - (9 - sequence) * 0.10f);
    }

    public void onParticleSeen(ServerPlayer player) {
        if (player.hasEffect(MobEffects.BLINDNESS))
            return;

        int sequence = BeyonderData.getSequence(player);

        float digestionGain = (1 + random.nextInt(2)) / 200f;
        BeyonderData.digest(player, digestionGain, false);

        // Always show a honorific fragment
        sendHonorificFragment(player);

        float chance = negativeEffectChance(sequence);
        if (random.nextFloat() > chance)
            return;

        player.addEffect(new MobEffectInstance(NAUSEA));

        List<MobEffectInstance> shuffled = new ArrayList<>(EXTRA_EFFECTS);
        Collections.shuffle(shuffled, new java.util.Random());
        int count = 1 + random.nextInt(3);
        for (int i = 0; i < Math.min(count, shuffled.size()); i++) {
            player.addEffect(new MobEffectInstance(shuffled.get(i)));
        }

        player.getData(ModAttachments.SANITY_COMPONENT)
                .increaseSanityAndSync(
                        -0.1f * (float) BeyonderData.getSanityDecreaseMultiplierForSequence(sequence),
                        player);
    }

    // Honorific name fragment
    private void sendHonorificFragment(ServerPlayer player) {
        if (BeyonderData.beyonderMap == null)
            return;

        List<StoredData> candidates = BeyonderData.beyonderMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(d -> {
                    HonorificName name = d.honorificName();
                    return name != null
                            && !name.isEmpty()
                            && name.lines() != null
                            && name.lines().size() >= 2;
                })
                .toList();

        if (candidates.isEmpty())
            return;

        StoredData chosen = candidates.get(random.nextInt(candidates.size()));
        List<String> lines = chosen.honorificName().lines();

        int maxStart = lines.size() - 2;
        int start = maxStart > 0 ? random.nextInt(maxStart + 1) : 0;

        String line1 = lines.get(start);
        String line2 = lines.get(start + 1);

        Component message = Component.empty()
                .append(Component.literal(line1).withStyle(style ->
                        style.withColor(0xbd64d1).withItalic(true)))
                .append(Component.literal("\n"))
                .append(Component.literal(line2).withStyle(style ->
                        style.withColor(0xbd64d1).withItalic(true)));

        player.sendSystemMessage(message);
    }

    @Override
    public void tick(Level level, LivingEntity entity) {}

    private static boolean isHermit(Player player) {
        return BeyonderData.getPathway(player).equals("hermit")
                && BeyonderData.getSequence(player) <= 9;
    }
}