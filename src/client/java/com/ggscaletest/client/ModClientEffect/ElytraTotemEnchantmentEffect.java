package com.ggscaletest.client.ModClientEffect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ElytraTotemEnchantmentEffect {
    public static final byte ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS = 115;

    public static void play(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 1. 播放混合音效:灵魂沙行走 + 恶魂死亡
        client.world.playSound(
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.BLOCK_SOUL_SAND_STEP,
                SoundCategory.PLAYERS,
                1.2F, 0.9F, false);
        client.world.playSound(
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENTITY_GHAST_DEATH,
                SoundCategory.PLAYERS,
                0.8F, 1.1F, false);

        // 2. 添加紫色传送门颗粒
        for (int i = 0; i < 40; i++) {
            double dx = (entity.getWorld().getRandom().nextDouble() - 0.5) * 2;
            double dy = (entity.getWorld().getRandom().nextDouble() - 0.5) * 2;
            double dz = (entity.getWorld().getRandom().nextDouble() - 0.5) * 2;

            client.particleManager.addParticle(
                    ParticleTypes.PORTAL,
                    entity.getX(),
                    entity.getY() + 1,
                    entity.getZ(),
                    dx * 0.3,
                    dy * 0.2 + 0.1,
                    dz * 0.3);
        }

        // 3. 显示浮动的鞘翅物品
        if (entity == client.player)

        {
            ItemStack chestStack = client.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);

            ItemStack displayStack = chestStack.getItem() instanceof net.minecraft.item.ElytraItem
                    ? chestStack
                    : new ItemStack(net.minecraft.item.Items.ELYTRA);

            client.gameRenderer.showFloatingItem(displayStack);
        }
    }
}
