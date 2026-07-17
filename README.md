# 🪶 Elytra Music 鞘翅之声

> **鞘翅飞行音乐 + 鞘翅图腾生存机制** — 为你的鞘翅注入忠诚的守护之魂  
> *Custom flight music + Elytra totem survival mechanics — Give your elytra a guardian soul*

---

## 📖 模组简介 | Overview

**Elytra Music & Totem** 为 Minecraft 原版鞘翅拓展了两大核心玩法：

- 🎵 **飞行音乐系统**：鞘翅滑翔时自动播放本地自定义音乐，支持平滑淡入淡出  
- 🛡️ **鞘翅图腾机制**：让鞘翅在致命伤害时成为你的“第二道防线”，通过三种形态与专属附魔构建完整的保命体系

**Elytra Music & Totem** enhances the vanilla elytra experience with two core features:

- 🎵 **Flight Music System** – Automatically play custom music from a local folder while gliding, with smooth fade‑in/fade‑out  
- 🛡️ **Elytra Totem Mechanic** – Turn your elytra into a second line of defense against fatal damage, through three distinct forms and a unique enchantment

---

## ✨ 核心功能 | Core Features

### 一、自定义飞行音乐 | Custom Flight Music

- 鞘翅滑翔时**自动切换**为自定义音乐，停止飞行后**平滑恢复**原版背景音乐  
- 在游戏根目录创建 `elytra_custom_music_folder/` 文件夹，放入 `.ogg` 格式音频即可  
- **随机播放** + **淡入淡出**（默认 5 秒，可配置）  
- 播放时屏幕上方显示当前曲目名称

- Automatically **switches to custom music** when gliding with an elytra, and resumes vanilla background music when you stop  
- Place `.ogg` audio files in `elytra_custom_music_folder/` (created automatically in the game root directory)  
- **Random playback** with **smooth crossfade** (default 5 seconds, configurable)  
- Shows the current track name on the HUD (reuses the vanilla jukebox overlay)

---

### 二、鞘翅图腾 —— 三种形态进化链 | Elytra Totem – Three‑Form Evolution Chain

| 形态 / Form | 获取方式 / How to Obtain | 保命机制 / Survival Mechanic | 触发后果 / Consequence on Trigger |
|-------------|--------------------------|-----------------------------|-----------------------------------|
| **原版鞘翅**<br>Vanilla Elytra | 末地船战利品 / End ship loot | 触发时消耗大量耐久换取一次图腾效果，转化为去图腾化鞘翅 / Trigger consumes large durability to get an totem effect, and transform to the untotemized elytra| 保留 **1/3 耐久（最低 18）** | — |
| **预图腾化鞘翅**<br>Totemized Elytra | 原版鞘翅 + 不死图腾（铁砧）<br>Vanilla Elytra + Totem of Undying (anvil) | 自带一次图腾效果 / Built‑in totem effect | 触发后变为原版鞘翅，**保留所有附魔与数据**<br>Becomes a vanilla elytra after triggering, **preserving all enchantments and data** |
| **去图腾化鞘翅**<br>Untotemized Elytra | 普通鞘翅触发图腾后自动转换<br>Automatically converted from a vanilla elytra after totem trigger | **不可使用经验修补**；可用不死图腾在铁砧修复为满耐久原版鞘翅<br>Retains **1/3 durability (minimum 18)**, **cannot be repaired with Mending**; can be fully repaired back to a vanilla elytra using a Totem of Undying on an anvil |

**铁砧配方 / Anvil Recipes：**

| 输入（左）/ Left Input | 输入（右）/ Right Input | 输出 / Output | 经验消耗 / Experience Cost |
|------------------------|-------------------------|---------------|----------------------------|
| 原版鞘翅 / Vanilla Elytra | 不死图腾 / Totem of Undying | 预图腾化鞘翅 / Totemized Elytra | 1 级（可配置）/ 1 level (configurable) |
| 去图腾化鞘翅 / Untotemized Elytra | 不死图腾 / Totem of Undying | 原版鞘翅（满耐久）/ Vanilla Elytra (full durability) | 6 级（可配置）/ 6 levels (configurable) |

---

### 三、专属附魔：「翅灵庇佑」| Exclusive Enchantment: *Protection from the Elytra*

> 让原版鞘翅拥有“最后一道防线”的能力  
> *A last‑resort safeguard for your vanilla elytra*

| 属性 / Attribute | 说明 / Description |
|------------------|---------------------|
| **适用物品 / Applicable to** | 仅限原版鞘翅（`minecraft:elytra`）<br>Only vanilla elytra |
| **获取途径 / How to Obtain** | 末地城宝藏箱（默认概率 10%，可配置）<br>End City treasure chests (10% chance by default, configurable) |
| **最大等级 / Max Level** | I |

**触发机制 / Trigger Logic：**

当玩家穿着带有此附魔的鞘翅受到致命伤害时：

1. **优先消耗背包中的不死图腾** → 触发原版图腾特效，附魔保留  
2. **若背包无图腾** → 消耗自身（移除附魔）代替图腾，触发专属特效（紫色传送门粒子 + 灵魂沙/恶魂音效），鞘翅本体完整保留

When a player wearing an elytra with this enchantment takes fatal damage:

1. **Prioritises consuming a Totem of Undying from the inventory** → triggers the vanilla totem effect (green particles) and **keeps the enchantment**  
2. **If no totem is in the inventory** → consumes itself (removes the enchantment) as a substitute, triggers a custom effect (purple portal particles + soul sand / ghast death sounds), and **leaves the elytra intact** (no durability loss, no form conversion)

> 💡 该附魔相当于在鞘翅上**额外存储了一次保命机会**，作为图腾耗尽时的最终防线。  
> *This enchantment effectively stores **one extra totem charge** on your elytra, serving as a final safety net when your totem supply runs out.*

---

## 🎮 生存模式使用指南 | Survival Mode Guide

### 第一步：安装 | Step 1: Installation
- 需要 **Fabric Loader 0.19.2+** + **Fabric API**  
- 将模组 JAR 放入 `mods/` 文件夹  
- Requires **Fabric Loader 0.19.2+** and **Fabric API** – Place the mod JAR in your `mods/` folder

### 第二步：添加自定义音乐 | Step 2: Add Custom Music
1. 运行一次游戏，根目录自动生成 `elytra_custom_music_folder/`  
2. 将 `.ogg` 音乐文件放入该文件夹  
3. 穿上鞘翅滑翔，即可自动播放！  
1. Run the game once – the folder `elytra_custom_music_folder/` will be created in the game root directory  
2. Place your `.ogg` music files into that folder  
3. Glide with an elytra and enjoy your custom soundtrack!

### 第三步：打造鞘翅图腾 | Step 3: Forge Your Elytra Totem

| 目标 / Goal | 操作 / Action |
|-------------|---------------|
| 获取预图腾化鞘翅 / Obtain a Totemized Elytra | 铁砧：原版鞘翅 + 不死图腾<br>Anvil: Vanilla Elytra + Totem of Undying |
| 修复去图腾化鞘翅 / Repair an Untotemized Elytra | 铁砧：去图腾化鞘翅 + 不死图腾（恢复满耐久）<br>Anvil: Untotemized Elytra + Totem of Undying (restores full durability) |
| 获取翅灵庇佑附魔书 / Get the Protection from the Elytra enchantment book | 探索末地城宝藏箱 / Explore End City treasure chests |

---

## ⚙️ 配置文件 | Configuration

路径 / File location：`/config/elytra-music-config.json`（首次运行自动生成 / auto‑generated on first run）

```json
{
  "anvil": {
    "repairCost": 6,       // 去图腾化 → 原版鞘翅 修复经验消耗 / Repair cost: Untotemized → Vanilla
    "repairCost_2": 1      // 原版 → 预图腾化鞘翅 合成经验消耗 / Recipe cost: Vanilla → Totemized
  },
  "music": {
    "fadeSeconds": 5.0     // 音乐淡入淡出时长（秒）/ Fade duration (seconds)
  },
  "enchantment": {
    "bookChance": 0.1      // 末地城宝箱出附魔书概率（0~1）/ Probability (0–1) of finding the book in End City chests
  }
}
```

---

## 📋 物品 ID 一览 | Item IDs

| 物品 / Item | ID |
|-------------|-----|
| 预图腾化鞘翅 / Totemized Elytra | `elytra-music:totemized_elytra` |
| 去图腾化鞘翅 / Untotemized Elytra | `elytra-music:untotemized_elytra` |
| 翅灵庇佑附魔 / Protection from the Elytra enchantment | `elytra-music:elytra_totem_enchantment` |

---

## ⚠️ 注意事项 | Notes

- **音乐格式 / Music format**：仅支持 `.ogg`（Vorbis 编码）/ Only `.ogg` (Vorbis) is supported  
- **音乐播放为客户端侧功能**，其他玩家不会听到你的音乐 / **Music playback is client‑side** – other players will not hear your music  
- **翅灵庇佑附魔无法通过附魔台获得**，只能通过末地城战利品箱 / The enchantment **cannot be obtained from an enchanting table** – it is only found in End City loot  
- 与修改鞘翅渲染/耐久逻辑的其他模组可能存在冲突 / May conflict with other mods that modify elytra rendering or durability logic

---

## 📜 开源许可 | License

**MIT License** —— 欢迎自由使用、修改与分发。  
*Free to use, modify, and distribute.*

---

*愿每一次滑翔都有音乐相伴，每一次绝境都有鞘翅守护。*  
*May every glide be accompanied by music, and every peril be shielded by your elytra.* 🪶
