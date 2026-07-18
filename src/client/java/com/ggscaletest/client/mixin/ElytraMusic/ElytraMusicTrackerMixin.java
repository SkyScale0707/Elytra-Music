package com.ggscaletest.client.mixin.ElytraMusic;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ggscaletest.client.ElytraMusicFadeInstance;
import com.ggscaletest.client.LoadCustomMusic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/*
    本mixin的作用是将我们的自定义音乐播放逻辑注入到原版的MusicTracker中
    拦截原版音乐资源的播放,根据玩家是否在飞行中,切换播放自定义音乐和原版音乐
*/
@Mixin(MusicTracker.class)
public abstract class ElytraMusicTrackerMixin {
    // 继承自原版client/MusicTracker的字段
    @Shadow
    @Final
    private MinecraftClient client; // 用于访问声音管理器、世界、玩家等信息
    @Shadow
    @Nullable
    private SoundInstance current; // 当前正在播放的音乐实例
    @Shadow
    private int timeUntilNextSong; // 距离下一首歌播放的tick数(倒计时)

    // 自定义字段
    private static final int ELYTRA_MIN_DELAY = 100; // 鞘翅音乐播放的最小间隔时间
    private static final int ELYTRA_MAX_DELAY = 200; // 鞘翅音乐播放的最大间隔时间
    @Nullable
    private static Identifier currentElytraMusicId = null;

    /* onTick()---核心注入方法 */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    // 目标方法:MusicTracker.tick()
    private void onTick(CallbackInfo ci) {
        if (this.client.player == null || this.client.world == null) {
            return;
        } // AI提供:空值检查,确保玩家已进入世界,避免空指针异常

        // 鞘翅检测
        boolean isElytraFlying = this.client.player.isFallFlying() &&
                this.client.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
        // 用实例类型判断"当前是否播放鞘翅音乐"(不受布尔标志影响)
        boolean currentIsElytraMusic = this.current instanceof ElytraMusicFadeInstance;
        // 添加日志
        if (isElytraFlying) {
            com.ggscaletest.ElytraMusic.LOGGER.info("鞘翅飞行已被检测,函数触发");
        }
        if (isElytraFlying && !LoadCustomMusic.CUSTOM_MUSIC_IDS.isEmpty()) {
            ci.cancel();
            if (this.current != null && !currentIsElytraMusic) {
                // 只停止非鞘翅音乐(原版音乐),不打断正在淡出的鞘翅音乐
                this.client.getSoundManager().stop(this.current);
                this.current = null;
                com.ggscaletest.ElytraMusic.LOGGER.info("停止原版音乐，准备播放鞘翅音乐");
            }
            handleElytraMusic();
            com.ggscaletest.ElytraMusic.LOGGER.info("有自定义音乐,取消原版逻辑");
        } else if (currentIsElytraMusic) {
            ci.cancel();
            // AI言 关键修复:取消原版逻辑,防止原版 MusicTracker 停止正在淡出的声音
            String dimension = this.client.world != null
                    ? this.client.world.getRegistryKey().getValue().toString()
                    : "unknown";
            com.ggscaletest.ElytraMusic.LOGGER.info(
                    "onTick 触发停止鞘翅音乐: 维度={}, isElytraFlying={}, currentIsElytraMusic={}",
                    dimension, isElytraFlying, currentIsElytraMusic);
            stopElytraMusic();
            this.timeUntilNextSong = ELYTRA_MIN_DELAY; // 重置延迟
            com.ggscaletest.ElytraMusic.LOGGER.info("停止鞘翅音乐,恢复原版");
        }
    }

    // 方法:handleElytraMusic() - 处理鞘翅音乐逻辑
    private void handleElytraMusic() {
        com.ggscaletest.ElytraMusic.LOGGER.info("handleElytraMusic() 被调用");
        com.ggscaletest.ElytraMusic.LOGGER.info("  current 是否为空：" + (this.current == null));
        com.ggscaletest.ElytraMusic.LOGGER.info("  timeUntilNextSong：" + this.timeUntilNextSong);
        // 如果音乐是首次播放,那么立即开始: this.timeUntilNextSong = 0
        if (this.current != null && !this.client.getSoundManager().isPlaying(this.current)) {
            com.ggscaletest.ElytraMusic.LOGGER.info("  检测到死引用,立即清理: current={}", this.current);
            this.client.getSoundManager().stop(this.current);
            this.current = null;
            currentElytraMusicId = null;
        }

        if (this.current == null && this.timeUntilNextSong > ELYTRA_MIN_DELAY) {
            com.ggscaletest.ElytraMusic.LOGGER.info("  第一次播放，立即开始！");
            this.timeUntilNextSong = 0;
        }
        if (this.current != null) {
            // 检查是否为正在淡出的鞘翅音乐,且已完成
            if (this.current instanceof ElytraMusicFadeInstance fadeMusic) {
                if (fadeMusic.isDone()) {
                    // 淡出完成,立即停止并清理,设置短延迟后开始下一首
                    this.client.getSoundManager().stop(this.current);
                    this.current = null;
                    this.timeUntilNextSong = 0; // 立即开始下一首(用户正在飞行!)
                    com.ggscaletest.ElytraMusic.LOGGER.info("  淡出完成,准备下一首音乐");
                }
            }
            // 检查音乐是否自然播放完毕(非淡出情况)
            // 音乐播放完毕后,设置随机延迟(5-10秒)再播放下一首
            if (this.current != null && !this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong,
                        MathHelper.nextInt(this.client.world.random, ELYTRA_MIN_DELAY, ELYTRA_MAX_DELAY));
            }
        }
        // 触发播放新音乐
        // 当没有音乐在播放且倒计时结束时,播放新音乐
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, ELYTRA_MAX_DELAY);
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            com.ggscaletest.ElytraMusic.LOGGER.info("  准备调用 playElytraMusic()");
            playElytraMusic();
        }
    }

    // 方法:playElytraMusic() - 播放鞘翅音乐
    private void playElytraMusic() {
        String dimension = this.client.world != null
                ? this.client.world.getRegistryKey().getValue().toString()
                : "unknown";
        com.ggscaletest.ElytraMusic.LOGGER.info("playElytraMusic() 被调用, 维度={}", dimension);
        // 获取随机音乐ID
        Identifier musicId = LoadCustomMusic.getRandomMusicId();
        com.ggscaletest.ElytraMusic.LOGGER.info("  现随机选择的音乐ID：" + musicId);
        if (musicId == null) {
            return;
        }
        // 创建音乐事件
        SoundEvent soundEvent = SoundEvent.of(musicId);
        // 使用我们创建的ElytraMusicFadeInstance淡入淡出音乐实例
        ElytraMusicFadeInstance musicInstance = new ElytraMusicFadeInstance(
                soundEvent,
                SoundCategory.MUSIC,
                dimension);
        if (musicInstance.getSound() != SoundManager.MISSING_SOUND) {
            com.ggscaletest.ElytraMusic.LOGGER.info("  开始播放音乐");
            // MISSING_SOUND:这是一个安全的占位符
            /*
             * ==防看不懂指南==//
             * 用处:
             * 1.防止空指针 当声音资源不存在时，返回一个有效对象而非 null
             * 2.静默失败 不会抛出异常，让程序继续运行
             * 3.标识符明确 使用 minecraft:empty 表示"这里应该有个声音，但没有"
             * 4.行为可控 后续代码可以通过 != MISSING_SOUND 判断是否真正有效
             * 定义:
             * MISSING_SOUND = new Sound(EMPTY_ID, ConstantFloatProvider.create(1.0F),
             * ConstantFloatProvider.create(1.0F), 1, RegistrationType.FILE, false, false,
             * 16);
             */
            // 使用这个占位,可以在放入的音乐文件损坏时,mod仍然可以正常运行,不会throw异常,使客户端崩溃
            this.client.getSoundManager().play(musicInstance);
            this.current = musicInstance; // 将音乐实例同步到current字段中
            currentElytraMusicId = musicId;
            // 声音有效,可以播放
            // 显示当前播放的音乐ID
            // 这里复用原版的唱片显示方法
            String musicName = LoadCustomMusic.getMusicDisplayName(musicId);
            net.minecraft.text.Text displayText = net.minecraft.text.Text.literal(musicName);
            this.client.inGameHud.setRecordPlayingOverlay(displayText);
        } else {
            com.ggscaletest.ElytraMusic.LOGGER.warn("  Sound 是 MISSING_SOUND！");
        }
        // 设置播放延迟为最大值,由此等待音乐自然结束
        this.timeUntilNextSong = Integer.MAX_VALUE;
        // 这里Integer.MAX_VALUE复用原版写法
    }

    // 方法:stopElytraMusic() - 停止鞘翅音乐(支持平滑淡出)
    private void stopElytraMusic() {
        String dimension = this.client.world != null
                ? this.client.world.getRegistryKey().getValue().toString()
                : "unknown";
        if (this.current instanceof ElytraMusicFadeInstance fadeMusic) {
            com.ggscaletest.ElytraMusic.LOGGER.info(
                    "stopElytraMusic() [{}]: current是FadeInstance, isFadingOut={}, isDone={}",
                    dimension, fadeMusic.isFadingOut(), fadeMusic.isDone());
            if (!fadeMusic.isFadingOut()) {
                // 首次调用:启动淡出
                fadeMusic.startFadeOut();
                com.ggscaletest.ElytraMusic.LOGGER.info("stopElytraMusic() [{}]: 启动淡出", dimension);
            }
            // 淡出完成后清理,释放引用,让原版音乐接管
            if (fadeMusic.isDone()) {
                this.client.getSoundManager().stop(this.current);
                this.current = null;
                com.ggscaletest.ElytraMusic.LOGGER.info("stopElytraMusic() [{}]: 淡出完成,已清理", dimension);
            }
        } else if (this.current != null) {
            // 非淡入淡出实例,直接停止
            com.ggscaletest.ElytraMusic.LOGGER.info(
                    "stopElytraMusic() [{}]: current非FadeInstance,直接停止", dimension);
            this.client.getSoundManager().stop(this.current);
            this.current = null;
        } else {
            com.ggscaletest.ElytraMusic.LOGGER.info(
                    "stopElytraMusic() [{}]: current为null,无操作", dimension);
        }
        currentElytraMusicId = null;
    }

}
