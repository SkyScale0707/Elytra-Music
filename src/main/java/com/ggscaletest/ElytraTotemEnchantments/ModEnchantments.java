package com.ggscaletest.ElytraTotemEnchantments;

import com.ggscaletest.ElytraMusic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    private static Enchantment register(String id, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(ElytraMusic.MOD_ID, id), enchantment);
    }

    public static final Enchantment ELYTRA_TOTEM_ENCHANTMENT = register("elytra_totem_enchantment",
            new ElytraTotemEnchantment());

    public static void initializer() {

    }

}
