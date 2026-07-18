package com.ggscaletest.mixin.TotemTriggerMethod;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ggscaletest.ElytraMusic;
import com.ggscaletest.ElytraTotemEnchantments.ModEnchantments;
import com.ggscaletest.ElytraTotemItems.ModItems;
import com.ggscaletest.ElytraTotemItems.TotemizedElytra;
import com.ggscaletest.ElytraTotemItems.UntotemizedElytra;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(LivingEntity.class)
public abstract class CheckTotemTriggerMixin {
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void afterTryUseTotem(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof PlayerEntity player)) {
            return;
        }
        if (player.getHealth() > 0.0F) {
            return;
        }
        if (tryEnchantedElytraTotem(player, source)) {
            player.setHealth(1.0F);
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 450, 1));

            ItemStack chest = player.getInventory().getArmorStack(2);
            boolean consumedTotem = tryConsumeTotemFromInventory(player);
            if (consumedTotem) {
                player.getWorld().sendEntityStatus(player, net.minecraft.entity.EntityStatuses.USE_TOTEM_OF_UNDYING);
            } else {
                player.getWorld().sendEntityStatus(player, ElytraMusic.ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS);
                removeElytraTotemEnchantment(chest);
            }

            cir.setReturnValue(true);
            return;
        }
        if (tryElytraTotem(player, source)) {
            player.setHealth(1.0F);
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 450, 1));

            player.getWorld().sendEntityStatus(player, ElytraMusic.CUSTOM_TOTEM_TRIGGER_STATUS);
            cir.setReturnValue(true);
            return;
        }

        if (tryTotemizedElytra(player, source)) {
            player.setHealth(1.0F);
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 450, 1));

            player.getWorld().sendEntityStatus(player, net.minecraft.entity.EntityStatuses.USE_TOTEM_OF_UNDYING);

            cir.setReturnValue(true);
            return;
        }
    }

    private boolean tryElytraTotem(PlayerEntity player, DamageSource source) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        if (!(chest.getItem() instanceof ElytraItem)) {
            return false;
        }
        if (chest.getItem() instanceof UntotemizedElytra) {
            return false;
        }
        if (chest.getItem() instanceof TotemizedElytra) {
            return false;
        }

        // 改为 NBT 方式, 检查是否有鞘翅图腾附魔
        if (EnchantmentHelper.getLevel(ModEnchantments.ELYTRA_TOTEM_ENCHANTMENT, chest) > 0) {
            return false;
        }

        int currentDamage = chest.getDamage();
        int maxDurability = chest.getMaxDamage();
        int remainingDurability = maxDurability - currentDamage;
        if (remainingDurability < 18) {
            return false;
        }

        int newRemainingDurability = Math.max(remainingDurability / 3, 18);
        int totalDamage = maxDurability - newRemainingDurability;
        chest.setDamage(totalDamage);

        transformToUnTotemizedElytra(player, chest);
        return true;
    }

    private boolean tryConsumeTotemFromInventory(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                stack.decrement(1);
                return true;
            }
        }
        return false;
    }

    private void transformToUnTotemizedElytra(PlayerEntity player, ItemStack oldElytra) {
        ItemStack newElytra = new ItemStack(ModItems.UNTOTEMIZED_ELYTRA);
        // 改为 NBT 方式, 复制 NBT
        if (oldElytra.hasNbt()) {
            newElytra.setNbt(oldElytra.getNbt().copy());
        }
        newElytra.setDamage(oldElytra.getDamage());
        removeMendingEnchantment(newElytra);

        // 自定义名称也通过 NBT 复制了，不需要单独处理

        player.getInventory().armor.set(2, newElytra);
    }

    // 改为 NBT 方式, 移除经验修补附魔
    private void removeMendingEnchantment(ItemStack stack) {
        if (stack.getEnchantments().isEmpty()) {
            return;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        enchantments.remove(Enchantments.MENDING);
        EnchantmentHelper.set(enchantments, stack);
    }

    private boolean tryTotemizedElytra(PlayerEntity player, DamageSource source) {
        ItemStack chest = player.getInventory().getArmorStack(2);

        if (!(chest.getItem() instanceof TotemizedElytra)) {
            return false;
        }

        if (chest.getDamage() >= chest.getMaxDamage()) {
            return false;
        }

        ItemStack newElytra = new ItemStack(Items.ELYTRA);

        // 改为 NBT 方式, 复制 NBT
        if (chest.hasNbt()) {
            newElytra.setNbt(chest.getNbt().copy());
        }
        newElytra.setDamage(chest.getDamage());

        player.getInventory().armor.set(2, newElytra);
        return true;
    }

    private boolean tryEnchantedElytraTotem(PlayerEntity player, DamageSource source) {
        ItemStack chest = player.getInventory().getArmorStack(2);

        if (!chest.isOf(Items.ELYTRA)) {
            return false;
        }

        // 改为 NBT 方式, 检查附魔
        boolean hasTotemEnchant = EnchantmentHelper.getLevel(ModEnchantments.ELYTRA_TOTEM_ENCHANTMENT, chest) > 0;
        if (!hasTotemEnchant) {
            return false;
        }

        if (chest.getDamage() >= chest.getMaxDamage()) {
            return false;
        }

        return true;
    }

    // 改为 NBT 方式, 移除鞘翅图腾附魔
    private void removeElytraTotemEnchantment(ItemStack stack) {
        if (stack.getEnchantments().isEmpty()) {
            return;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        enchantments.remove(ModEnchantments.ELYTRA_TOTEM_ENCHANTMENT);
        EnchantmentHelper.set(enchantments, stack);
    }
}
