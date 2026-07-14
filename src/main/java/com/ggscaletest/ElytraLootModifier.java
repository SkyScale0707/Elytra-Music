package com.ggscaletest;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

// 为末地城战利品列表add自定义附魔书: ElytraTotemEnchantment
public class ElytraLootModifier {
    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!LootTables.END_CITY_TREASURE_CHEST.equals(key)) {
                // 只处理 end_city_treasure 这个战利品表
                return;
            }

            // 获取附魔的RegistryEntry, 即ID: elytra_totem_enchantment
            Optional<RegistryEntry<Enchantment>> enchantment = registries
                    .getOptionalWrapper(RegistryKeys.ENCHANTMENT)
                    .flatMap(registry -> registry.getOptional(ModEnchantment.ELYTRA_TOTEM_ENCHANTMENT));

            // 如果附魔存在, 则构建一个新的pool
            enchantment.ifPresent(enchantmentEntry -> {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(BinomialLootNumberProvider.create(1, ElytraMusicConfig.enchantmentBookChance))
                        // roll一次, 每次有ElytraMusicConfig.enchantmentBookChance的概率得到该附魔书
                        // 可自定义ElytraMusicConfig.enchantmentBookChance, 默认0.1
                        .with(ItemEntry.builder(Items.ENCHANTED_BOOK))
                        .apply(new SetEnchantmentsLootFunction.Builder() // 应用附魔效果
                                .enchantment(enchantmentEntry, ConstantLootNumberProvider.create(1)));
                tableBuilder.pool(pool);
                // 将新构建的pool添加到战利品表中
                // 这是追加操作,不会影响已有的 pool
            });
        });
    }
}
