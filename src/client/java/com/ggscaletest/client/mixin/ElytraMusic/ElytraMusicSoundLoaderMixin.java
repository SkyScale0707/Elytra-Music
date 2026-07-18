package com.ggscaletest.client.mixin.ElytraMusic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ggscaletest.client.LoadCustomMusic;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.util.Identifier;

/*
  这个 Mixin 的核心作用是拦截游戏的声音加载器
  让它能从我们自定义的文件夹加载音乐文件
  而不是只从资源包中加载

  工作原理:
  1. 当 SoundSystem 尝试播放流式音乐时，会调用 SoundLoader.loadStreamed()
  2. 我们在这个方法调用前（HEAD）拦截它
  3. 如果是我们模组的命名空间（elytra-music），就从文件系统加载
  4. 否则让原版逻辑继续处理
 */
@Mixin(SoundLoader.class)
public abstract class ElytraMusicSoundLoaderMixin {
    @Inject(method = "loadStreamed", at = @At("HEAD"), cancellable = true)
    public void onLoadStreamed(Identifier id, boolean repeatInstantly,
            CallbackInfoReturnable<CompletableFuture<AudioStream>> cir) {

        com.ggscaletest.ElytraMusic.LOGGER.info("SoundLoader 尝试加载：" + id);
        // 检测命名空间: 是否为本mod的namespace
        if (id.getNamespace().equals(LoadCustomMusic.MOD_ID)) {
            // 这里在进行测试时出现了无法加载音乐文件的问题
            // 经过TracAI的检验,她给出以下建议:
            // 问题：Sound.getLocation() 会自动加上 sounds/ 前缀和 .ogg 后缀
            // 例如：elytra-music:testcustommusic1 → elytra-music:sounds/testcustommusic1.ogg
            // 我们需要把它还原成原来的 ID
            // 步骤 1：获取原始路径
            String customMusicPath = id.getPath();
            // 步骤 2：去除可能的 sounds/ 前缀
            if (customMusicPath.startsWith("sounds/")) {
                customMusicPath = customMusicPath.substring("sounds/".length());
            }
            // 步骤 3：去除可能的 .ogg 后缀
            if (customMusicPath.endsWith(".ogg")) {
                customMusicPath = customMusicPath.substring(0, customMusicPath.length() - ".ogg".length());
            }
            // 步骤 4：用还原后的路径重新构建 ID
            Identifier originalId = new Identifier(LoadCustomMusic.MOD_ID, customMusicPath);
            com.ggscaletest.ElytraMusic.LOGGER.info("  还原后的原始 ID：{}", originalId);

            File audioFile = LoadCustomMusic.getMusicFile(originalId);
            com.ggscaletest.ElytraMusic.LOGGER.info("  对应文件：" + audioFile.getAbsolutePath());
            com.ggscaletest.ElytraMusic.LOGGER.info("  文件存在：" + (audioFile != null && audioFile.exists()));
            if (audioFile != null && audioFile.exists()) {
                com.ggscaletest.ElytraMusic.LOGGER.info("  加载文件！");
                // CompletableFuture.supplyAsync() 表示这个加载操作会在后台线程异步执行
                cir.setReturnValue(CompletableFuture.supplyAsync(() -> {
                    try {
                        InputStream inputStream = new FileInputStream(audioFile);
                        // 使用游戏自带的 OggAudioStream 来解码音频
                        return new OggAudioStream(inputStream);
                    } catch (Exception e) {
                        com.ggscaletest.ElytraMusic.LOGGER.error("加载失败：", e);
                        throw new RuntimeException(e);
                        // 平滑处理加载失败的音乐实例
                    }
                }));
            }
        }
    }
}
