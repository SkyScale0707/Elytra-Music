package com.ggscaletest.ModEnchantmentEffect;

import com.mojang.serialization.MapCodec;

import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

// 自定义附魔效果: elytra_totem_enchantment 鞘翅图腾附魔效果

// 实现EnchantmentEntityEffect接口
public record ElytraTotemEnchantmentEffect() implements EnchantmentEntityEffect {

    // 实现EnchantmentEntityEffect要我们重写两个方法，其中一个是CODEC，是编解码器，
    // 这个是高版本都有的一个东西，用于数据传输

    // 实现apply方法, 用来指定附魔会有什么效果
    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {

    }

    public static final MapCodec<ElytraTotemEnchantmentEffect> CODEC = MapCodec.unit(ElytraTotemEnchantmentEffect::new);

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}
