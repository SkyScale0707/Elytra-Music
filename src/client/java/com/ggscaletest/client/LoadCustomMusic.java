package com.ggscaletest.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class LoadCustomMusic {
    // 基础声明:MOD_ID
    public static final String MOD_ID = "elytra-music";
    // 音乐文件存储文件夹名
    public static final String CUSTOM_MUSIC_FOLDER = "elytra_custom_music_folder";
    // 读取音乐命名字符串数组, 为了使用原版播放逻辑, 类型使用Identifier
    public static final List<Identifier> CUSTOM_MUSIC_IDS = new ArrayList<>();
    // 随机数生成
    public static final Random RANDOM = new Random();

    // 函数:读取自定义音乐文件
    public static void loadCustomMusics() {
        // 清空字符串数组内容
        CUSTOM_MUSIC_IDS.clear();

        // 获取游戏根目录
        File gameDir = net.minecraft.client.MinecraftClient.getInstance().runDirectory;

        // 初始化custom_music_folder,并提供定位文件夹路径的功用
        File musicDir = new File(gameDir, CUSTOM_MUSIC_FOLDER);
        // 添加日志
        com.ggscaletest.ElytraMusic.LOGGER.info("加载自定义音乐文件夹：" + musicDir.getAbsolutePath());
        if (!musicDir.exists()) {
            musicDir.mkdirs();
        }

        // 遍历音乐文件(目前仅支持ogg格式,我也不知道原生代码是否支持mp3,反正贴近原版最稳定awa)
        // 如果找到文件,将它们转换为音乐标识符并添加到列表
        File[] files = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".ogg"));
        // 检查File[]数组的内容日志
        com.ggscaletest.ElytraMusic.LOGGER.info("files 数组：" + (files != null ? "长度 " + files.length : "null"));
        if (files != null) {
            for (File file : files) {
                // 去掉文件名的 .ogg 后缀
                String fileName = file.getName().replace(".ogg", "");
                // 创建 Identifier (格式："elytra-music:文件名")
                CUSTOM_MUSIC_IDS.add(new Identifier(MOD_ID, fileName));
                com.ggscaletest.ElytraMusic.LOGGER
                        .info("添加音乐文件：" + fileName + " -> ID：" + new Identifier(MOD_ID, fileName));
            }
        }
    }

    // 函数:随机获取存入CUSTOM_MUSIC_IDS的音乐
    public static Identifier getRandomMusicId() {
        if (CUSTOM_MUSIC_IDS.isEmpty()) {
            return null;
        }
        return CUSTOM_MUSIC_IDS.get(RANDOM.nextInt(CUSTOM_MUSIC_IDS.size()));
    }

    // 函数:根据获取的Identifier获取音乐文件
    public static File getMusicFile(Identifier id) {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File musicDir = new File(gameDir, CUSTOM_MUSIC_FOLDER);
        String fileName = id.getPath() + ".ogg";
        return new File(musicDir, fileName);
    }

    // 函数: 获取目前播放音乐的ID
    public static String getMusicDisplayName(Identifier musicId) {
        // 名称直接由getMusicFile接入即可
        String musicName = musicId.getPath();
        return musicName;
    }
}
