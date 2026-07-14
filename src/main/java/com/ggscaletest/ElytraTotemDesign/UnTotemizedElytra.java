package com.ggscaletest.ElytraTotemDesign;

import org.jetbrains.annotations.NotNull;
import java.util.List;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UnTotemizedElytra extends ElytraItem implements FabricElytraItem {
    public UnTotemizedElytra() {
        // 设置最大耐久为 432（原版鞘翅的耐久值）
        super(new Item.Settings().maxDamage(432));
    }

    // 附魔检查部分: 不允许get经验修补!
    @Override
    public boolean canBeEnchantedWith(@NotNull ItemStack stack,
            @NotNull RegistryEntry<Enchantment> enchantment,
            @NotNull EnchantingContext context) {
        // 如果是经验修补附魔,直接拒绝
        if (enchantment.matchesKey(Enchantments.MENDING)) {
            return false;
        }
        // 其他附魔调用父类方法FabricItem.canBeEnchantedWith处理
        return super.canBeEnchantedWith(stack, enchantment, context);
    }

    // tooltip 信息
    @Override
    public void appendTooltip(@NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Text> tooltip,
            @NotNull TooltipType type) {
        // 添加红色提示,提醒玩家这是-去图腾化-的鞘翅
        tooltip.add(Text.translatable("item.ggscaletest.untotemized_elytra.tooltip1")
                .formatted(Formatting.RED));
        tooltip.add(Text.translatable("item.ggscaletest.untotemized_elytra.tooltip2")
                .formatted(Formatting.DARK_RED));
        tooltip.add(Text.translatable("item.ggscaletest.untotemized_elytra.tooltip3")
                .formatted(Formatting.DARK_PURPLE));
        // 调用父类方法保留其他提示
        super.appendTooltip(stack, context, tooltip, type);
    }

    // 装备类型继承
    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.CHEST;
    }

    // 修复类型继承
    @Override
    public boolean canRepair(@NotNull ItemStack stack, @NotNull ItemStack ingredient) {
        return ingredient.isOf(net.minecraft.item.Items.PHANTOM_MEMBRANE);
    }
}
