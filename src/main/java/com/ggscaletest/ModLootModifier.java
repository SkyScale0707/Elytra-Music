package com.ggscaletest;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class ModLootModifier {
    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // 只处理 end_city_treasure 这个战利品表
            if (!LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
                return;
            }

            // 1.20.1 直接用 Enchantment 对象, 不需要 RegistryEntry
            LootPool.Builder pool = LootPool.builder()
                    .rolls(BinomialLootNumberProvider.create(1, ElytraMusicConfig.enchantmentBookChance))
                    // roll一次, 每次有 enchantmentBookChance 的概率得到该附魔书, 默认概率为0.1
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK))
                    .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(
                                    com.ggscaletest.ElytraTotemEnchantments.ModEnchantments.ELYTRA_TOTEM_ENCHANTMENT,
                                    ConstantLootNumberProvider.create(1)));

            tableBuilder.pool(pool);
        });
    }
}
