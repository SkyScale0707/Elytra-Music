// ElytraFadeMusicInstance.java
package com.ggscaletest.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class ElytraMusicFadeInstance extends AbstractSoundInstance implements TickableSoundInstance {

    // 淡入淡出参数
    private final float fadeSteps; // 淡入淡出持续tick数(从配置读取)
    private static final float MIN_VOLUME = 0.0F;
    private static final float MAX_VOLUME = 1.0F;

    // 当前淡入淡出音量
    // 旧实现(仅通过 getVolume() 返回, SoundSystem 可能未使用):
    // private float currentFadeVolume = MAX_VOLUME;
    // private float fadeProgress = 1.0F;
    private static final float START_VOLUME = 0.001F;
    private float currentFadeVolume = START_VOLUME;

    private boolean fadingOut = false;
    private boolean fadingIn = true;

    // 旧实现:
    // private float fadeProgress = 1.0F;
    // private boolean done = false;
    private float fadeProgress = START_VOLUME;
    private boolean done = false;

    // 保存原始音量,用于在 tick() 中更新 this.volume
    // 旧实现: this.volume 从未被修改,仅通过 getVolume() 计算返回
    private float baseVolume = 1.0F;

    private final String dimension;

    public ElytraMusicFadeInstance(SoundEvent sound, SoundCategory category, String dimension) {
        super(sound, category, Random.create());
        this.fadeSteps = com.ggscaletest.ElytraMusicConfig.fadeSteps; // 从配置读取
        this.dimension = dimension;
        // 保存原始音量,后续 tick() 中会基于此值做淡入淡出
        // 旧实现: 未保存 baseVolume,仅通过 getVolume() 动态计算
        this.baseVolume = this.volume;
    }

    @Override
    public void tick() {
        com.ggscaletest.ElytraMusic.LOGGER.info(
                "TICK [{}]: progress={}, volume={}, fadingIn={}, fadingOut={}, done={}",
                dimension, fadeProgress, currentFadeVolume, fadingIn, fadingOut, done);
        if (done) {
            return; // 已完成,不再处理
        }
        if (fadingIn) {
            // 淡入阶段:从 0 → 1.0
            fadeProgress += 1.0F / fadeSteps;
            if (fadeProgress >= 1.0F) {
                fadeProgress = 1.0F;
                fadingIn = false;
            }
            currentFadeVolume = MIN_VOLUME + (MAX_VOLUME - MIN_VOLUME) * fadeProgress;
        } else if (fadingOut) {
            // 淡出阶段:从 1.0 → 0
            fadeProgress -= 1.0F / fadeSteps;
            if (fadeProgress <= 0.0F) {
                fadeProgress = 0.0F;
                fadingOut = false;
                done = true; // 淡出完成,标记为 done
            }
            currentFadeVolume = MIN_VOLUME + (MAX_VOLUME - MIN_VOLUME) * Math.max(0, fadeProgress);
        }
        // 关键修改:直接更新 this.volume 字段,确保 SoundSystem 能感知到音量变化
        // 旧实现: 仅通过 getVolume() 返回计算值,this.volume 从未改变
        this.volume = baseVolume * currentFadeVolume;
    }

    /*
     * 旧实现:仅通过 getVolume() 返回计算值, this.volume 从未被修改
     * 
     * @Override
     * public float getVolume() {
     *     // 返回淡入淡出音量,而非原始音量
     *     return currentFadeVolume * this.volume;
     * }
     */

    @Override
    public float getVolume() {
        // 新实现: this.volume 已在 tick() 中被更新为 baseVolume * currentFadeVolume
        // 直接返回即可,确保 SoundSystem 无论通过哪种方式获取音量都能得到正确值
        float v = this.volume;
        com.ggscaletest.ElytraMusic.LOGGER.info(
                "GET_VOLUME [{}]: currentFadeVolume={}, baseVolume={}, this.volume={}, result={}",
                dimension, currentFadeVolume, baseVolume, this.volume, v);
        return v;
    }

    @Override
    public boolean isDone() {
        return done; // 使用 done 标志,可靠判断是否完成

    }

    // 开始淡出
    public void startFadeOut() {
        fadingOut = true;
        fadingIn = false;
    }

    // 是否正在淡出
    public boolean isFadingOut() {
        return fadingOut;
    }
}