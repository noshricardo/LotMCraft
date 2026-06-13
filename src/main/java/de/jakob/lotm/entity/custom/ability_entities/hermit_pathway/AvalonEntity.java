package de.jakob.lotm.entity.custom.ability_entities.hermit_pathway;

import de.jakob.lotm.util.helper.AllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class AvalonEntity extends Entity {

    private static final int RADIUS = 30;
    private static final int OWNER_MAX_DISTANCE = 70;

    // Track one active Avalon per owner
    private static final Map<UUID, AvalonEntity> ACTIVE_BY_OWNER = new HashMap<>();

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(AvalonEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private BlockPos centerPos;
    private final java.util.Random random = new java.util.Random();

    public AvalonEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_UUID, Optional.empty());
    }

    public void setOwner(LivingEntity owner) {
        this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
    }

    @Nullable
    public LivingEntity getOwner() {
        Optional<UUID> uuid = this.entityData.get(OWNER_UUID);
        if (uuid.isPresent() && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid.get());
            if (entity instanceof LivingEntity living) return living;
        }
        return null;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    /**
     * Removes any existing Avalon for this owner and registers the new one.
     */
    public static void registerForOwner(UUID ownerUUID, AvalonEntity entity) {
        AvalonEntity existing = ACTIVE_BY_OWNER.get(ownerUUID);
        if (existing != null && !existing.isRemoved()) {
            existing.discard();
        }
        ACTIVE_BY_OWNER.put(ownerUUID, entity);
    }

    /**
     * Whether an entity is allowed inside Avalon:
     * - The owner themselves
     * - Allies of the owner
     * - Anyone holding a nether star
     */
    private boolean isAllowed(Entity entity) {
        LivingEntity owner = getOwner();
        if (owner == null) return true;

        UUID ownerUUID = owner.getUUID();

        // Owner is always allowed
        if (entity.getUUID().equals(ownerUUID)) return true;

        // Nether star pass
        if (entity instanceof Player player) {
            boolean hasNetherStar = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(Items.NETHER_STAR));
            if (hasNetherStar) return true;
        }

        // Ally check
        if (entity instanceof LivingEntity living) {
            return AllyUtil.areAllies(owner, living);
        }

        return false;
    }

    /**
     * Pushes an entity to just outside the sphere boundary.
     */
    private void ejectEntity(Entity entity) {
        Vec3 center = Vec3.atCenterOf(centerPos);
        Vec3 entityPos = entity.position();
        Vec3 direction = entityPos.subtract(center);

        if (direction.lengthSqr() < 0.001) {
            // Entity is at exact center — pick a random outward direction
            direction = new Vec3(1, 0, 0);
        }

        Vec3 ejectionPos = center.add(direction.normalize().scale(RADIUS + 1.5));

        // Find a safe Y position
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos ejectionBlock = BlockPos.containing(ejectionPos);
            while (!serverLevel.getBlockState(ejectionBlock).isAir() && ejectionBlock.getY() < serverLevel.getMaxBuildHeight()) {
                ejectionBlock = ejectionBlock.above();
            }
            ejectionPos = new Vec3(ejectionPos.x, ejectionBlock.getY(), ejectionPos.z);
        }

        entity.teleportTo(ejectionPos.x, ejectionPos.y, ejectionPos.z);
        entity.setDeltaMovement(Vec3.ZERO);

        if (entity instanceof ServerPlayer player) {
            player.connection.resetPosition();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        centerPos = this.blockPosition();

        LivingEntity owner = getOwner();

        // Discard if owner is gone or too far
        if (owner == null || owner.isRemoved() || !owner.isAlive()) {
            this.discard();
            return;
        }
        if (!owner.level().equals(this.level())) {
            this.discard();
            return;
        }
        if (owner.distanceToSqr(this) > OWNER_MAX_DISTANCE * OWNER_MAX_DISTANCE) {
            this.discard();
            return;
        }

        // Only enforce boundaries every 5 ticks to reduce cost
        if (this.tickCount % 5 != 0) return;

        Vec3 center = Vec3.atCenterOf(centerPos);
        double radiusSq = RADIUS * RADIUS;

        // Spawn a few enchantment glint particles randomly on the sphere surface
        for (int i = 0; i < 50; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double px = center.x + RADIUS * Math.sin(phi) * Math.cos(theta);
            double py = center.y + RADIUS * Math.sin(phi) * Math.sin(theta);
            double pz = center.z + RADIUS * Math.cos(phi);
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    px, py, pz, 20, 0, 0, 0, 0);
        }

        AABB area = new AABB(centerPos).inflate(RADIUS);
        List<Entity> entities = serverLevel.getEntities(this, area,
                e -> e instanceof LivingEntity && e != owner);

        for (Entity entity : entities) {
            double distSq = entity.position().distanceToSqr(center);
            if (distSq <= radiusSq && !isAllowed(entity)) {
                ejectEntity(entity);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID != null) {
            ACTIVE_BY_OWNER.remove(ownerUUID, this);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        // Not persistent
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        // Not persistent
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}