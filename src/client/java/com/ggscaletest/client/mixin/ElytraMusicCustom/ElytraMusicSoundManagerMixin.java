package com.ggscaletest.client.mixin.ElytraMusicCustom;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ggscaletest.ElytraMusic;
import com.ggscaletest.client.ElytraMusicClient;

/*
 * 这个 Mixin 的作用是拦截 SoundManager.get() 方法
 * 当查询 elytra-music 命名空间的声音时,返回一个自定义的 WeightedSoundSet
 * 这样 SoundSystem 就会认为我们的声音是有效的,进而调用 SoundLoader.loadStreamed()
 */
@Mixin(SoundManager.class)
public class ElytraMusicSoundManagerMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void onGetSound(Identifier id, CallbackInfoReturnable<WeightedSoundSet> cir) {
        // 检查是否是我们模组的命名空间
        if (id.getNamespace().equals(ElytraMusicClient.MOD_ID)) {
            ElytraMusic.LOGGER.info("SoundManager.get() 被调用，查询: {}", id);

            // 检查这个 ID 是否在我们的音乐列表中
            if (ElytraMusicClient.CUSTOM_MUSIC_IDS.contains(id)) {
                ElytraMusic.LOGGER.info("  找到自定义音乐，返回自定义 WeightedSoundSet");

                // 创建一个自定义的 WeightedSoundSet
                WeightedSoundSet soundSet = new WeightedSoundSet(id, null);

                // 创建一个 Sound 对象
                // 参数：
                // - id: 声音标识符
                // - volume: 音量（常量 1.0）
                // - pitch: 音高（常量 1.0）
                // - weight: 权重（1）
                // - registrationType: 注册类型（FILE）
                // - stream: 是否流式（true - 音乐应该是流式的）
                // - preload: 是否预加载（false）
                // - attenuation: 衰减类型（16 - 原版音乐的值）
                Sound sound = new Sound(
                        id,
                        ConstantFloatProvider.create(1.0F),
                        ConstantFloatProvider.create(1.0F),
                        1,
                        Sound.RegistrationType.FILE,
                        true, // 关键：标记为流式声音
                        false,
                        16);

                // 将 Sound 添加到 WeightedSoundSet
                soundSet.add(sound);

                // 返回我们的自定义 SoundSet
                cir.setReturnValue(soundSet);
            } else {
                ElytraMusic.LOGGER.info("  未在 CUSTOM_MUSIC_IDS 中找到，返回 null");
            }
        }
    }
}