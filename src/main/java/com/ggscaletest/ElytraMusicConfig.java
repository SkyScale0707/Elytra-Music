package com.ggscaletest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// 原汁原味的从1.21.1的项目中搬过来 (Crlt+V) awa
public class ElytraMusicConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "elytra-music-config.json";

    // 配置项
    public static int anvilRepairCost = 6; // 去图腾化鞘翅 → 原版鞘翅 的修复费用
    public static int anvilRepairCost_2 = 1; // 去图腾化鞘翅 → 预图腾化鞘翅 的修复费用
    public static float fadeSeconds = 5.0F; // 淡入淡出持续秒数（配置文件读取）
    public static float fadeSteps = 100.0F; // 淡入淡出持续tick数（fadeSeconds * 20）
    public static float enchantmentBookChance = 0.1F; // 末地城宝箱刷新鞘翅图腾附魔书的概率

    /**
     * 加载配置文件
     */
    public static void load() {
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE).toFile();

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                if (json.has("anvil")) {
                    JsonObject anvil = json.getAsJsonObject("anvil");
                    anvilRepairCost = getIntOrDefault(anvil, "repairCost", anvilRepairCost);
                    anvilRepairCost_2 = getIntOrDefault(anvil, "repairCost_2", anvilRepairCost_2);
                }

                if (json.has("music")) {
                    JsonObject music = json.getAsJsonObject("music");
                    fadeSeconds = getFloatOrDefault(music, "fadeSeconds", fadeSeconds);
                }

                if (json.has("enchantment")) {
                    JsonObject enchantment = json.getAsJsonObject("enchantment");
                    enchantmentBookChance = getFloatOrDefault(enchantment, "bookChance", enchantmentBookChance);
                }

                // 计算淡入淡出tick数
                fadeSteps = fadeSeconds * 20.0F;

                ElytraMusic.LOGGER.info(
                        "配置文件已加载: repairCost={}, repairCost_2={}, fadeSeconds={}, fadeSteps={}, enchantmentBookChance={}",
                        anvilRepairCost, anvilRepairCost_2, fadeSeconds, fadeSteps, enchantmentBookChance);
            } catch (Exception e) {
                ElytraMusic.LOGGER.error("加载配置文件失败，使用默认值", e);
            }
        } else {
            // 创建默认配置文件
            saveDefault(configFile);
        }
    }

    private static int getIntOrDefault(JsonObject json, String key, int defaultValue) {
        if (json.has(key)) {
            return json.get(key).getAsInt();
        }
        return defaultValue;
    }

    private static float getFloatOrDefault(JsonObject json, String key, float defaultValue) {
        if (json.has(key)) {
            return json.get(key).getAsFloat();
        }
        return defaultValue;
    }

    /**
     * 保存默认配置文件
     */
    private static void saveDefault(File configFile) {
        // 计算默认 fadeSteps
        fadeSteps = fadeSeconds * 20.0F;

        try (FileWriter writer = new FileWriter(configFile)) {
            JsonObject json = new JsonObject();
            JsonObject anvil = new JsonObject();
            anvil.addProperty("repairCost", anvilRepairCost);
            anvil.addProperty("repairCost_2", anvilRepairCost_2);
            json.add("anvil", anvil);

            JsonObject music = new JsonObject();
            music.addProperty("fadeSeconds", fadeSeconds);
            json.add("music", music);

            JsonObject enchantment = new JsonObject();
            enchantment.addProperty("bookChance", enchantmentBookChance);
            json.add("enchantment", enchantment);

            GSON.toJson(json, writer);
            ElytraMusic.LOGGER.info("已创建默认配置文件: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            ElytraMusic.LOGGER.error("创建默认配置文件失败", e);
        }
    }
}