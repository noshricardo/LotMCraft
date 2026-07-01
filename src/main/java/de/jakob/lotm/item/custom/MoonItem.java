package de.jakob.lotm.item.custom;

import de.jakob.lotm.entity.custom.ability_entities.BigMoonEntity;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.DamageLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MoonItem extends Item {
    public MoonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if(!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }

        player.getCooldowns().addCooldown(player.getItemInHand(usedHand), 20 * 35);

        setTimeToMidnight(serverLevel);
        BigMoonEntity moonEntity = new BigMoonEntity(serverLevel, (float) DamageLookup.lookupDps(2, .7f, 2, 20) * (float) BeyonderData.getMultiplierForSequence(2), BeyonderData.isGriefingEnabled(player), player.getUUID(), 20 * 30);
        moonEntity.setPos(player.getX(), player.getY() + 25, player.getZ());
        serverLevel.addFreshEntity(moonEntity);

        if(!player.getAbilities().instabuild) {
            ItemStack itemStack = player.getItemInHand(usedHand);
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    private void setTimeToMidnight(ServerLevel serverLevel) {
        long currentTime = serverLevel.getDayTime();
        long currentDayTime = currentTime % 24000;

        long targetTime;
        targetTime = 18000;

        // Calculate ticks to add to reach target time
        long ticksToAdd;
        if (currentDayTime <= targetTime) {
            // Target time hasn't occurred yet today
            ticksToAdd = targetTime - currentDayTime;
        } else {
            // Target time has already passed, advance to target time tomorrow
            ticksToAdd = (24000 - currentDayTime) + targetTime;
        }

        serverLevel.setDayTime(currentTime + ticksToAdd);
    }
}
