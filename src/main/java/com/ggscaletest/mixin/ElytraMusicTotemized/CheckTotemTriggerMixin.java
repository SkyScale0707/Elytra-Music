package com.ggscaletest.mixin.ElytraMusicTotemized;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ggscaletest.ElytraTotemDesign.ModItems;
import com.ggscaletest.ElytraTotemDesign.TotemizedElytra;
import com.ggscaletest.ElytraTotemDesign.UnTotemizedElytra;
// import com.ggscaletest.client.ElytraTotemEffect;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import com.ggscaletest.ModEnchantment;
import net.minecraft.registry.entry.RegistryEntry;

// Mixin: 鞘翅图腾 触发方法Mixin实现
@Mixin(LivingEntity.class)
public abstract class CheckTotemTriggerMixin {
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z", shift = At.Shift.AFTER), cancellable = true)
    // 函数: 鞘翅图腾触发流程,为避免与旧方法名冲突,方法名改为afterTryUseTotem
    private void afterTryUseTotem(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof PlayerEntity player)) {
            return; // 仅针对玩家
        }
        if (player.getHealth() > 0.0F) {
            return; // 防止误触发
        }
        if (tryEnchantedElytraTotem(player, source)) {
            // 鞘翅图腾附魔触发效果实现
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
                // 使用鞘翅图腾附魔专属状态码(514),播放灵魂沙+恶魂死亡音效和紫色传送门颗粒
                player.getWorld().sendEntityStatus(player,
                        com.ggscaletest.ElytraMusic.ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS);
                // 未消耗图腾则移除鞘翅图腾附魔
                removeElytraTotemEnchantment(chest);
            }

            cir.setReturnValue(true);
            return;
        }
        if (tryElytraTotem(player, source)) {
            // 类似图腾触发效果实现
            player.setHealth(1.0F);
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 450, 1));
            // 这里使用自定义设计: ElytraTotemEffect
            // 由于服务端不能引用客户端的方法, 因此我们在ElytraMusic中将其共享
            /*
             * // 由于已经引入自定义附魔,故废弃该方案
             * if (tryConsumeTotemFromInventory(player)) {
             * player.getWorld().sendEntityStatus(player,
             * net.minecraft.entity.EntityStatuses.USE_TOTEM_OF_UNDYING);
             * } else {
             * player.getWorld().sendEntityStatus(player,
             * com.ggscaletest.ElytraMusic.CUSTOM_TOTEM_TRIGGER_STATUS);
             * }
             */
            player.getWorld().sendEntityStatus(player,
                    com.ggscaletest.ElytraMusic.CUSTOM_TOTEM_TRIGGER_STATUS);
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

    // 函数: 检查,计算触发后果
    private boolean tryElytraTotem(PlayerEntity player, DamageSource source) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        if (!(chest.getItem() instanceof ElytraItem)) {
            return false;
        }
        if (chest.getItem() instanceof UnTotemizedElytra) {
            return false;
        }
        if (chest.getItem() instanceof TotemizedElytra) {
            return false;
        }

        // 筛查鞘翅上的附魔组件,若含有鞘翅图腾附魔则不触发本方法
        if (chest.contains(DataComponentTypes.ENCHANTMENTS)) {
            ItemEnchantmentsComponent enchantments = chest.getEnchantments();
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                if (entry.matchesKey(ModEnchantment.ELYTRA_TOTEM_ENCHANTMENT)) {
                    return false;
                }
            }
        }

        // 触发后耐久惩罚实现
        int currentDamage = chest.getDamage();
        int maxDurability = chest.getMaxDamage();
        int remainingDurability = maxDurability - currentDamage;
        if (remainingDurability < 18) {
            // 耐久限制: 若少于18, 鞘翅图腾不生效
            return false;
        }

        int newRemainingDurability = Math.max(remainingDurability / 3, 18);

        int totalDamage = maxDurability - newRemainingDurability;
        chest.setDamage(totalDamage);

        /*
         * // 由于已经引入自定义附魔,故废弃该方案
         * boolean haveTotem = tryConsumeTotemFromInventory(player);
         * 
         * if (!haveTotem) {
         * if (!(chest.getItem() instanceof TotemizedElytra)) {
         * transformToUnTotemizedElytra(player, chest);
         * }
         * 
         * }
         */
        transformToUnTotemizedElytra(player, chest);

        return true;
    }

    // 函数: 尝试使用背包的图腾
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

    // 函数: 若背包中无图腾,转换为去图腾化的鞘翅
    private void transformToUnTotemizedElytra(PlayerEntity player, ItemStack oldElytra) {
        ItemStack newElytra = new ItemStack(ModItems.UNTOTEMIZED_ELYTRA);
        newElytra.applyComponentsFrom(oldElytra.getComponents());
        newElytra.setDamage(oldElytra.getDamage());
        removeMendingEnchantment(newElytra);

        // 原鞘翅信息和组件保留, 并完整继承到去图腾化的鞘翅上
        if (oldElytra.contains(DataComponentTypes.CUSTOM_NAME)) {
            newElytra.set(DataComponentTypes.CUSTOM_NAME, oldElytra.getName());
        }

        player.getInventory().armor.set(2, newElytra);
    }

    // 函数: 移除经验修补附魔
    private void removeMendingEnchantment(ItemStack stack) {
        if (!stack.contains(DataComponentTypes.ENCHANTMENTS)) {
            return;
        }
        ItemEnchantmentsComponent currentEnchantments = stack.getEnchantments();
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(currentEnchantments);
        // 运用lambda函数处理
        builder.remove(entry -> entry.matchesKey(Enchantments.MENDING));
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
    }

    // 函数: 使用/触发预图腾化的鞘翅
    private boolean tryTotemizedElytra(PlayerEntity player, DamageSource source) {
        ItemStack chest = player.getInventory().getArmorStack(2);

        // 条件1: 必须装备预图腾化鞘翅
        if (!(chest.getItem() instanceof TotemizedElytra)) {
            return false;
        }

        // 条件2: 检查耐久（至少需要1点耐久来消耗）
        if (chest.getDamage() >= chest.getMaxDamage()) {
            return false; // 已损坏，无法触发
        }

        // 转换为原版鞘翅, 继承其所有组件和耐久值
        ItemStack newElytra = new ItemStack(Items.ELYTRA);

        newElytra.applyComponentsFrom(chest.getComponents());

        newElytra.setDamage(chest.getDamage());

        player.getInventory().armor.set(2, newElytra);

        return true;
    }

    // 函数: 鞘翅图腾附魔触发方法
    private boolean tryEnchantedElytraTotem(PlayerEntity player, DamageSource source) {
        ItemStack chest = player.getInventory().getArmorStack(2);

        // 条件1: 必须是原版鞘翅
        if (!chest.isOf(Items.ELYTRA)) {
            return false;
        }

        // 条件2: 检查是否带有elytra_totem_enchantment附魔
        boolean hasTotemEnchant = false;
        if (chest.contains(DataComponentTypes.ENCHANTMENTS)) {
            ItemEnchantmentsComponent enchantments = chest.getEnchantments();
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                if (entry.matchesKey(ModEnchantment.ELYTRA_TOTEM_ENCHANTMENT)) {
                    hasTotemEnchant = true;
                    break;
                }
            }
        }
        if (!hasTotemEnchant) {
            return false;
        }

        // 条件3: 检查耐久,至少需要1点耐久
        if (chest.getDamage() >= chest.getMaxDamage()) {
            return false;
        }

        return true;
    }

    // 方法: 移除elytra_totem_enchantment附魔
    private void removeElytraTotemEnchantment(ItemStack stack) {
        if (!stack.contains(DataComponentTypes.ENCHANTMENTS)) {
            return;
        }
        ItemEnchantmentsComponent currentEnchantments = stack.getEnchantments();
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(currentEnchantments);
        builder.remove(entry -> entry.matchesKey(ModEnchantment.ELYTRA_TOTEM_ENCHANTMENT));
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
    }
}
