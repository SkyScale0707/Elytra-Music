package com.ggscaletest.client.ModClientEffect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

/*
case 35:
    int i = 40;
    // 1. 添加图腾粒子效果
    this.client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
    // 2. 播放图腾音效
    this.world.playSound(entity.getX(), entity.getY(), entity.getZ(), 
                         SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 
                         1.0F, 1.0F, false);
    // 3. 如果是本地玩家，显示浮动的图腾物品
    if (entity == this.client.player) {
        this.client.gameRenderer.showFloatingItem(getActiveTotemOfUndying(this.client.player));
    }
    break;
*/
public class ElytraTotemEffect {
    // 自定义触发效果的状态码
    public static final byte CUSTOM_TOTEM_TRIGGER_STATUS = 114;

    public static void play(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 1. 播放混合音效
        client.world.playSound(
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ITEM_TOTEM_USE,
                SoundCategory.PLAYERS,
                1.2F, 0.9F, false);
        client.world.playSound(
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ITEM_ELYTRA_FLYING,
                SoundCategory.PLAYERS,
                0.6F, 1.3F, false);

        // 2. 添加粒子
        // AI设计的,我实在不愿意搞事情了
        for (int i = 0; i < 30; i++) {
            double dx = (entity.getWorld().getRandom().nextDouble() - 0.5) * 2;
            double dy = (entity.getWorld().getRandom().nextDouble() - 0.5) * 2;
            double dz = (entity.getWorld().getRandom().nextDouble() - 0.5) * 2;

            client.particleManager.addParticle(
                    ParticleTypes.END_ROD,
                    entity.getX(),
                    entity.getY() + 1,
                    entity.getZ(),
                    dx * 0.2,
                    dy * 0.2 + 0.1,
                    dz * 0.2);

            client.particleManager.addParticle(
                    ParticleTypes.ENTITY_EFFECT,

                    entity.getX(),
                    entity.getY() + 1,
                    entity.getZ(),
                    dx * 0.3,
                    dy * 0.3 + 0.1,
                    dz * 0.3);
        }

        // 3. 显示浮动的鞘翅物品
        if (entity == client.player)

        {
            // 显示玩家胸甲槽中鞘翅的浮动效果
            ItemStack chestStack = client.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);

            // 如果胸甲是鞘翅，显示鞘翅；否则显示默认鞘翅
            ItemStack displayStack = chestStack.getItem() instanceof net.minecraft.item.ElytraItem
                    ? chestStack
                    : new ItemStack(net.minecraft.item.Items.ELYTRA);

            client.gameRenderer.showFloatingItem(displayStack);
        }
    }
}
