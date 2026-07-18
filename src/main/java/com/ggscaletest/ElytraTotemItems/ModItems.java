package com.ggscaletest.ElytraTotemItems;

import com.ggscaletest.ElytraMusic;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ElytraMusic.MOD_ID, id), item);
    }

    // 去图腾化的鞘翅
    public static final Item UNTOTEMIZED_ELYTRA = register("untotemized_elytra", new UntotemizedElytra());

    // 图腾化的鞘翅
    public static final Item TOTEMIZED_ELYTRA = register("totemized_elytra", new TotemizedElytra());

    public static void initializer() {

    }
}
