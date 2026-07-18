package com.ggscaletest.ElytraTotemItems;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.client.item.TooltipContext;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class UntotemizedElytra extends ElytraItem implements FabricElytraItem {
    public UntotemizedElytra() {
        super(new Item.Settings().maxDamage(432));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.ggscaletest.untotemized_elytra.tooltip1")
                .formatted(Formatting.RED));
        tooltip.add(Text.translatable("item.ggscaletest.untotemized_elytra.tooltip2")
                .formatted(Formatting.DARK_RED));
        tooltip.add(Text.translatable("item.ggscaletest.untotemized_elytra.tooltip3")
                .formatted(Formatting.DARK_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(net.minecraft.item.Items.PHANTOM_MEMBRANE);
    }

    /*
     * API 里面居然没有现成的canBeEnchantedWith方法!
     * 哦,我无疑是愤怒的,不甘的......
     */
}
