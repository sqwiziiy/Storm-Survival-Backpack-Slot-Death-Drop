package com.mentality.stormsurvivalbackpackdeathcompat.mixin;

import com.mentality.stormsurvivalbackpackdeathcompat.StormSurvivalBackpackDeathCompat;
import com.mentality.stormsurvivalbackpackdeathcompat.mixin.BackpackDeathHelperInvoker;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Routes only the equipped Trinkets chest/back backpack through TB's native placement path.
 */
@Mixin(value = LivingEntity.class, priority = 1100)
public abstract class BackpackDeathTransferMixin {
    @Inject(method = "drop", at = @At("HEAD"), require = 1)
    private void stormSurvival$placeBackpack(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayerEntity player)) {
            return;
        }
        boolean keepInventory = player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
        StormSurvivalBackpackDeathCompat.LOGGER.info(
                "[Storm Backpack Death] terminal death hook entered player={} keepInventory={}",
                player.getGameProfile().getName(), keepInventory);
        if (keepInventory) {
            return;
        }

        TrinketsApi.getTrinketComponent(player).ifPresent(component ->
                transferBackSlot(player, component));
    }

    private static void transferBackSlot(ServerPlayerEntity player, TrinketComponent component) {
        Map<String, Map<String, TrinketInventory>> inventory = component.getInventory();
        Map<String, TrinketInventory> chest = inventory.get("chest");
        TrinketInventory back = chest == null ? null : chest.get("back");
        if (back == null) {
            StormSurvivalBackpackDeathCompat.LOGGER.info("[Storm Backpack Death] chest/back not present; no transfer");
            return;
        }

        for (int index = 0; index < back.size(); index++) {
            ItemStack equipped = back.getStack(index);
            if (!(equipped.getItem() instanceof TravelersBackpackItem) || equipped.isEmpty()) {
                continue;
            }

            ItemStack transfer = equipped.copy();
            var settings = TravelersBackpackConfig.getConfig().backpackSettings;
            boolean deathPlace = settings.backpackDeathPlace;
            boolean forceDeathPlace = settings.backpackForceDeathPlace;
            StormSurvivalBackpackDeathCompat.LOGGER.info(
                    "[Storm Backpack Death] back stack={} count={} backpackDeathPlace={} backpackForceDeathPlace={} native method={}",
                    transfer.getItem(), transfer.getCount(), deathPlace, forceDeathPlace,
                    forceDeathPlace ? "placeBackpack" : "tryPlace");
            if (!deathPlace) {
                StormSurvivalBackpackDeathCompat.LOGGER.info("[Storm Backpack Death] native placement disabled; slot unchanged");
                return;
            }

            boolean placed;
            try {
                placed = forceDeathPlace
                        ? BackpackDeathHelperInvoker.stormSurvivalBackpackDeathCompat$placeBackpack(
                        player.getWorld(), player, player.getBlockPos(), transfer)
                        : BackpackDeathHelperInvoker.stormSurvivalBackpackDeathCompat$tryPlace(
                        player.getWorld(), player, transfer);
            } catch (RuntimeException exception) {
                StormSurvivalBackpackDeathCompat.LOGGER.error(
                        "[Storm Backpack Death] native placement threw; slot unchanged", exception);
                return;
            }
            BlockPos placedPos = stormSurvival$findPlacedBackpack(player, transfer);
            StormSurvivalBackpackDeathCompat.LOGGER.info(
                    "[Storm Backpack Death] native placement success={} placedBlock={} duplicateSuppression=slot-clear-before-downstream",
                    placed, placedPos == null ? "unknown" : placedPos);
            if (!placed) {
                StormSurvivalBackpackDeathCompat.LOGGER.warn(
                        "[Storm Backpack Death] native placement failed; slot unchanged and no fallback ItemEntity created");
                return;
            }
            back.setStack(index, ItemStack.EMPTY);
            StormSurvivalBackpackDeathCompat.LOGGER.info(
                    "[Storm Backpack Death] slot clear success={} index={} TB/Graves duplicate path suppressed",
                    back.getStack(index).isEmpty(), index);
            return;
        }
    }

    private static BlockPos stormSurvival$findPlacedBackpack(ServerPlayerEntity player, ItemStack stack) {
        Block backpackBlock = Block.getBlockFromItem(stack.getItem());
        if (backpackBlock == null) return null;
        BlockPos origin = player.getBlockPos();
        for (int radius = 0; radius <= 12; radius++) {
            for (int y = -12; y <= 12; y++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                        BlockPos candidate = origin.add(dx, y, dz);
                        if (player.getWorld().getBlockState(candidate).isOf(backpackBlock)) return candidate;
                    }
                }
            }
        }
        return null;
    }
}
