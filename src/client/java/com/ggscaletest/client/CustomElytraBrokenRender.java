package com.ggscaletest.client;

import com.ggscaletest.ElytraTotemDesign.ModItems;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.Identifier;

public class CustomElytraBrokenRender {
    // 我们使 去图腾化的鞘翅拥有与原版相同的破损后渲染逻辑
    public static void registerCustomElytraBrokenRender() {
        ModelPredicateProviderRegistry.register(
                ModItems.UNTOTEMIZED_ELYTRA,
                Identifier.ofVanilla("broken"),
                (stack, world, entity, seed) -> ElytraItem.isUsable(stack) ? 0.0F : 1.0F);
        ModelPredicateProviderRegistry.register(
                ModItems.TOTEMIZED_ELYTRA,
                Identifier.ofVanilla("broken"),
                (stack, world, entity, seed) -> ElytraItem.isUsable(stack) ? 0.0F : 1.0F);
    }

    public static void initializer() {
        registerCustomElytraBrokenRender();
    }
}
