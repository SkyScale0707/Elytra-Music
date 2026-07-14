package com.ggscaletest.ElytraTotemDesign;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TotemizedElytra extends ElytraItem implements FabricElytraItem {

    public TotemizedElytra() {
        super(new Item.Settings().maxDamage(432));
    }

    // tooltip
    @Override
    public void appendTooltip(@NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Text> tooltip,
            @NotNull TooltipType type) {
        tooltip.add(Text.translatable("item.ggscaletest.totemized_elytra.tooltip1")
                .formatted(Formatting.DARK_PURPLE));
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
