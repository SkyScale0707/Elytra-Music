package com.ggscaletest.client.mixin.ModClientRender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ggscaletest.client.ModClientEffect.ElytraTotemEffect;
import com.ggscaletest.client.ModClientEffect.ElytraTotemEnchantmentEffect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

/*
* 客户端 Mixin,用于在客户端拦截服务端发来的"实体状态"数据包
* 当收到自定义状态码 (在ElytraTotemEffect中定义为了114......) 时,播放自定义鞘翅图腾效果.
*/

/*
 原先的设计:
 @Mixin(ClientPlayNetworkHandler.class)
    public class ElytraTotemEffectMixin {
    
    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity(((ClientPlayNetworkHandler)(Object)this).world);
        
        // 拦截自定义状态码
        if (packet.getStatus() == ElytraTotemEffect.CUSTOM_TOTEM_STATUS) {
            ElytraTotemEffect.play(entity);
        }
    }
}
在打开游戏客户端，第一次进入世界时会断连: java.lang.NullPointerException: Cannot invoke "net.minecraft.world.World.getEntityById(int)" because "world" is null
问过AI，他的回答如下:

第一次进入世界时， ClientPlayNetworkHandler 的 world 字段还没有初始化（是 null），但已经有 EntityStatusS2CPacket 数据包到达。
你的 Mixin 在方法最开头（ @At("HEAD") ）就调用了 packet.getEntity(world) ，
而 getEntity() 内部会调用 world.getEntityById() ，导致 world 为 null 时直接抛 NPE。
原版代码虽然也调用了 packet.getEntity(this.world) ，但它在后续有 if (entity != null) 过滤，
而且更关键的是——原版在数据包到达的 时序 上，通常 world 已经初始化好了。
但某些情况下（尤其是第一次加载世界时）会有早到的数据包。

以下是对其的安全化修改:
*/
@Mixin(ClientPlayNetworkHandler.class)
public class ElytraTotemEffectMixin {

    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        byte status = packet.getStatus();

        // 1. 先快速检查状态码, 不是本模组的就直接返回
        if (status != ElytraTotemEffect.CUSTOM_TOTEM_TRIGGER_STATUS
                && status != ElytraTotemEnchantmentEffect.ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS) {
            return;
        }
        // 2. 安全获取 world（通过 MinecraftClient 访问，避免访问还没初始化的字段）
        net.minecraft.world.World world = net.minecraft.client.MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }
        // 3. 再获取实体
        Entity entity = packet.getEntity(world);
        if (entity == null) {
            return;
        }

        // 4. 根据状态码播放对应效果
        // 交由主线程处理, 避免产生闪退
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (status == ElytraTotemEffect.CUSTOM_TOTEM_TRIGGER_STATUS) {
                ElytraTotemEffect.play(entity);
            } else if (status == ElytraTotemEnchantmentEffect.ELYTRA_TOTEM_ENCHANTMENT_TRIGGER_STATUS) {
                ElytraTotemEnchantmentEffect.play(entity);
            }
        });
    }
}