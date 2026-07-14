package com.ggscaletest;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantment {
    // 注册方法
    private static RegistryKey<Enchantment> of(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(ElytraMusic.MOD_ID, name));
    }

    // 自定义附魔: elytra_totem_enchantmentment 鞘翅图腾附魔
    // 命名待取
    public static final RegistryKey<Enchantment> ELYTRA_TOTEM_ENCHANTMENT = of("elytra_totem_enchantment");

    public static void initializer() {

    }
}
