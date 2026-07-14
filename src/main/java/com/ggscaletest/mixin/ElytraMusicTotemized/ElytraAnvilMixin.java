package com.ggscaletest.mixin.ElytraMusicTotemized;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ggscaletest.ElytraMusicConfig;
import com.ggscaletest.ElytraTotemDesign.UnTotemizedElytra;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;

import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;

// UntotemizedElytra 去图腾化鞘翅的修复配方
@Mixin(AnvilScreenHandler.class)
public abstract class ElytraAnvilMixin {
    // AnvilScreenHandler 中的字段
    @Shadow
    @Final
    private Property levelCost; // 修复费用

    @Shadow
    private int repairItemUsage; // 修复消耗材料数量

    // 注入点: 在 updateResult() 方法开头
    // 拦截铁砧操作,实现去图腾化鞘翅的恢复配方
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo info) {
        ScreenHandler handler = (ScreenHandler) (Object) this;
        // 获取铁砧的输入槽
        ItemStack leftInput = handler.getSlot(0).getStack(); // 左侧槽
        ItemStack rightInput = handler.getSlot(1).getStack(); // 右侧槽
        // 检查配方条件
        if (leftInput.isEmpty() || rightInput.isEmpty()) {
            return; // 槽位为空,则继续原版逻辑
        }
        // 条件1：左侧必须是去图腾化鞘翅
        if (!(leftInput.getItem() instanceof UnTotemizedElytra)) {
            return; // 若不是去图腾化鞘翅,继续原版逻辑
        }
        // 条件2：右侧必须是不死图腾
        if (!rightInput.isOf(Items.TOTEM_OF_UNDYING)) {
            return; // 若不是不死图腾,继续原版逻辑
        }

        // 修复配方
        // 1. 创建新的原版鞘翅(修复品)
        ItemStack newElytra = new ItemStack(Items.ELYTRA);
        // 2. 复制所有组件(包括附魔,自定义名称等)
        newElytra.applyComponentsFrom(leftInput.getComponents());
        // 3. 设置满耐久
        newElytra.setDamage(0);
        // 4. 设置经验费用（使用配置值）
        this.levelCost.set(ElytraMusicConfig.anvilRepairCost);
        // 5. 不死图腾不可堆叠，固定消耗1个
        this.repairItemUsage = 1;
        // 6. 设置输出物品
        handler.getSlot(2).setStack(newElytra);
        // 7. 取消原版逻辑
        info.cancel();

    }
}
