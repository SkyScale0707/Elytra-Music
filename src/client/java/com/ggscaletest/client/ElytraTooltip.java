package com.ggscaletest.client;

import java.util.List;

import com.ggscaletest.ElytraTotemEnchantments.ModEnchantments;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ElytraTooltip {
    public static void register() {
        ItemTooltipCallback.EVENT.register((ItemStack stack, TooltipContext context, List<Text> lines) -> {
            // 为原版鞘翅添加 tooltip
            if (stack.isOf(Items.ELYTRA)) {
                lines.add(Text.translatable("item.minecraft.elytra.tooltip")
                        .formatted(Formatting.GREEN));
                lines.add(Text.translatable("item.minecraft.elytra.tooltip2")
                        .formatted(Formatting.DARK_PURPLE));
            }

            // 为带有鞘翅图腾附魔的附魔书添加 tooltip
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                if (hasElytraTotemEnchantment(stack)) {
                    lines.add(Text.translatable("enchantment.elytra-music.elytra_totem_enchantment.desc")
                            .formatted(Formatting.LIGHT_PURPLE));
                }
            }
        });
    }

    // 改为 NBT 方式, 检查附魔书是否带有鞘翅图腾附魔
    private static boolean hasElytraTotemEnchantment(ItemStack stack) {
        return EnchantmentHelper.getLevel(ModEnchantments.ELYTRA_TOTEM_ENCHANTMENT, stack) > 0;
    }

}
