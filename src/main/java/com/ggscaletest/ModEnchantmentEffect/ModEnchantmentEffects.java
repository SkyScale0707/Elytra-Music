package com.ggscaletest.ModEnchantmentEffect;

import com.ggscaletest.ElytraMusic;
import com.mojang.serialization.MapCodec;

import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {
    public static void initializer() {

    }

    private static MapCodec<? extends EnchantmentEntityEffect> register(String name,
            MapCodec<? extends EnchantmentEntityEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(ElytraMusic.MOD_ID, name),
                codec);
    }

    public static final MapCodec<? extends EnchantmentEntityEffect> ELYTRA_TOTEM_ENCHANTMENT = register(
            "elytra_totem_enchantment",
            ElytraTotemEnchantmentEffect.CODEC);

}
