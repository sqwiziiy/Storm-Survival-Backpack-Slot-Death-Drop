package com.mentality.stormsurvivalbackpackdeathcompat.mixin;

import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Narrow access to TB 9.1.55's private static native placement operations. */
@Mixin(BackpackDeathHelper.class)
public interface BackpackDeathHelperInvoker {
    @Invoker("tryPlace")
    static boolean stormSurvivalBackpackDeathCompat$tryPlace(World world, PlayerEntity player, ItemStack stack) {
        throw new AssertionError();
    }

    @Invoker("placeBackpack")
    static boolean stormSurvivalBackpackDeathCompat$placeBackpack(World world, PlayerEntity player, BlockPos position, ItemStack stack) {
        throw new AssertionError();
    }
}
