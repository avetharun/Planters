package com.feintha.planters;

import com.feintha.planters.blocks.PlanterBlock;
import com.feintha.planters.blocks.entity.PlanterBlockEntity;
import com.feintha.planters.blocks.entity.renderer.PlanterBlockEntityRenderer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Arrays;

public class Planters implements ModInitializer {

    private static Block registerBlock(Block i, String name) {
        return Registry.register(Registries.BLOCK, new Identifier("planters", name), i);
    }
    private static Item registerItem(String name, Item i, RegistryKey<ItemGroup> group) {
        Item re_i = Registry.register(Registries.ITEM, new Identifier("planters", name), i);
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            content.add(re_i);
        });
        return re_i;
    }
    public static float mod(float a, float b){
        return ((a % b) + b) % b;
    }
    private static Item registerItem(String name, Item i, Pair<RegistryKey<ItemGroup>,  Item>... groups) {
        Item re_i = Registry.register(Registries.ITEM, new Identifier("planters", name), i);
        Arrays.stream(groups).forEach(registryKeyItemPair -> {
            ItemGroupEvents.modifyEntriesEvent(registryKeyItemPair.getLeft()).register(content -> {
                content.addAfter(registryKeyItemPair.getRight(),re_i);
            });
        });
        return re_i;
    }
    public static final BlockSoundGroup WOOD_PLANTER = new BlockSoundGroup(1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_GRAVEL_STEP, SoundEvents.BLOCK_WOOD_PLACE, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_GRAVEL_FALL);
    public static final PlanterBlock DIRT_PLANTER_BLOCK = Registry.register(Registries.BLOCK, new Identifier("planters:dirt_planter"), new PlanterBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).sounds(WOOD_PLANTER)));
    public static final PlanterBlock FARMLAND_PLANTER_BLOCK = Registry.register(Registries.BLOCK, new Identifier("planters:farmland_planter"), new PlanterBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).sounds(WOOD_PLANTER).nonOpaque()));
    public static final BlockItem DIRT_PLANTER_ITEM = (BlockItem) registerItem("dirt_planter", new BlockItem(DIRT_PLANTER_BLOCK, new Item.Settings()), new Pair<>(ItemGroups.NATURAL, Items.FARMLAND));
    public static final BlockItem FARMLAND_PLANTER_ITEM = (BlockItem) registerItem("farmland_planter", new BlockItem(FARMLAND_PLANTER_BLOCK, new Item.Settings()), new Pair<>(ItemGroups.NATURAL, Items.FARMLAND));
    public static final BlockEntityType<PlanterBlockEntity> PLANTER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("planters:planter_block_entity"), FabricBlockEntityTypeBuilder.create(PlanterBlockEntity::new, DIRT_PLANTER_BLOCK, FARMLAND_PLANTER_BLOCK).build());
    @Override
    public void onInitialize() {
        BlockRenderLayerMap.INSTANCE.putBlock(FARMLAND_PLANTER_BLOCK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(PLANTER_BLOCK_ENTITY, PlanterBlockEntityRenderer::new);
    }
}
