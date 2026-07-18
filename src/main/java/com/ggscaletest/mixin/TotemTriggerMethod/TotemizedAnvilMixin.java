package com.ggscaletest.mixin.TotemTriggerMethod;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ggscaletest.ElytraMusicConfig;
import com.ggscaletest.ElytraTotemItems.ModItems;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;

@Mixin(AnvilScreenHandler.class)
public abstract class TotemizedAnvilMixin {
    @Shadow
    @Final
    private Property levelCost;

    @Shadow
    private int repairItemUsage;

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo info) {
        ScreenHandler handler = (ScreenHandler) (Object) this;
        ItemStack leftInput = handler.getSlot(0).getStack();
        ItemStack rightInput = handler.getSlot(1).getStack();

        if (leftInput.isEmpty() || rightInput.isEmpty()) {
            return;
        }

        // 条件1：左侧必须是原版鞘翅
        if (!leftInput.isOf(Items.ELYTRA)) {
            return;
        }

        // 条件2：右侧必须是不死图腾
        if (!rightInput.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        // 合成结果：预图腾化鞘翅
        ItemStack newElytra = new ItemStack(ModItems.TOTEMIZED_ELYTRA);

        // 复制 NBT
        if (leftInput.hasNbt()) {
            newElytra.setNbt(leftInput.getNbt().copy());
        }

        // 继承原耐久
        newElytra.setDamage(leftInput.getDamage());

        // 设置经验费用
        this.levelCost.set(ElytraMusicConfig.anvilRepairCost_2);

        // 消耗 1 个不死图腾
        this.repairItemUsage = 1;

        // 设置输出
        handler.getSlot(2).setStack(newElytra);

        // 取消原版逻辑
        info.cancel();
    }
}
