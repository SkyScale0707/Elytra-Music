package com.ggscaletest.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import com.ggscaletest.ModEnchantment;

public class ElytraTooltip {
    public static void register() {
        // 为原版鞘翅添加 tooltip
        ItemTooltipCallback.EVENT
                .register((ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> tooltip) -> {
                    if (stack.isOf(Items.ELYTRA)) {
                        tooltip.add(Text.translatable("item.minecraft.elytra.tooltip")
                                .formatted(Formatting.GREEN));
                        tooltip.add(Text.translatable("item.minecraft.elytra.tooltip2")
                                .formatted(Formatting.DARK_PURPLE));
                    }
                    // 为带有鞘翅图腾附魔的附魔书添加 tooltip
                    if (stack.isOf(Items.ENCHANTED_BOOK)) {
                        if (hasElytraTotemEnchantment(stack)) {
                            tooltip.add(Text.translatable("enchantment.elytra-music.elytra_totem_enchantment.desc")
                                    .formatted(Formatting.LIGHT_PURPLE));
                        }
                    }
                });

    }

    // 辅助方法: 检查物品是否带有鞘翅图腾附魔
    private static boolean hasElytraTotemEnchantment(ItemStack stack) {
        if (!stack.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
            return false;
        }
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.matchesKey(ModEnchantment.ELYTRA_TOTEM_ENCHANTMENT)) {
                return true;
            }
        }
        return false;
    }

    public static void initializer() {
        register();
    }
}