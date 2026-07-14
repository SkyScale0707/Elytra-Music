package com.ggscaletest.client.mixin.ElytraMusicCustom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
// import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import com.ggscaletest.ElytraTotemDesign.TotemizedElytra;
import com.ggscaletest.ElytraTotemDesign.UnTotemizedElytra;

// 同时 Mixin 两个渲染器,处理两个渲染器的 isOf(Items.ELYTRA) 检查
// @Mixin(value = { ElytraFeatureRenderer.class, CapeFeatureRenderer.class })
@Mixin(ElytraFeatureRenderer.class)
public abstract class CustomElytraRenderMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean redirectIsOfElytra(ItemStack stack, Item item) {
        // 如果原本检查的是 Items.ELYTRA, 同时接受 UnTotemizedElytra 和 TotemizedElytra
        if (item == Items.ELYTRA) {
            return stack.isOf(Items.ELYTRA) || stack.getItem() instanceof UnTotemizedElytra
                    || stack.getItem() instanceof TotemizedElytra;
        }
        // 其他物品保持原版行为
        return stack.isOf(item);
    }
}