package com.ggscaletest.ElytraTotemEnchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraTotemEnchantment extends Enchantment {
    protected ElytraTotemEnchantment() {
        // 对应 JSON: "weight": 1 → Rarity.VERY_RARE
        // 对应 JSON: "slots": ["chest"]
        // public class ElytraItem extends Item implements Equipment
        // 使用BREAKABLE而非ARMOR_CHEST作为枚举继承对象
        super(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.CHEST });
    }

    // 对应 JSON: "max_level": 1
    @Override
    public int getMaxLevel() {
        return 1;
    }

    // 对应 JSON: "min_cost": { "base": 5, "per_level_above_first": 8 }
    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 8;
    }

    // 对应 JSON: "max_cost": { "base": 25, "per_level_above_first": 8 }
    @Override
    public int getMaxPower(int level) {
        return 25 + (level - 1) * 8;
    }

    // 对应 JSON: "anvil_cost": 2(铁砧合并费用)
    // 这个在 1.20.1 中通过 rarity 权重间接控制，不需要单独设置

    // 对应 JSON: "supported_items": ["minecraft:elytra"]
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isOf(Items.ELYTRA);
    }

    // 对应 JSON: effects -> damage_protection: base 1.5
    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        // 原版 ProtectionEnchantment 是每级 +1，这里给 +2 相当于 1.5 左右的保护
        return level * 2;
    }

    // 对应 JSON: effects -> repair_with_xp: 0(禁用经验修补)
    // 这个在 1.20.1 中不需要在附魔类中直接实现
    // 需要在物品类或 Mixin 中处理（后续移植时处理）

    // 对应 JSON: effects -> tick(死亡触发效果)
    // 这个通过后续在 Mixin 中处理, 即CheckTotemTriggerMixin
}
