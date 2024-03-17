package com.feintha.planters.mixin;
import com.feintha.planters.Planters;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class BlockPlaceOnMixins {
    @Mixin(SugarCaneBlock.class)
    public static class SugarCaneBlockMixin {
        @ModifyReturnValue(method = "canPlaceAt", at=@At("RETURN"))
        boolean canPlantOnTop(boolean original, BlockState state, WorldView world, BlockPos pos){
            return original || world.getBlockState(pos.down()).isOf(Planters.FARMLAND_PLANTER_BLOCK);
        }
    }
    @Mixin(SmallDripleafBlock.class)
    public static class SmallDripleafBlockMixin {
        @ModifyReturnValue(method = "canPlaceAt", at=@At("RETURN"))
        boolean canPlantOnTop(boolean original, BlockState state, WorldView world, BlockPos pos){
            return original || world.getBlockState(pos.down()).isOf(Planters.FARMLAND_PLANTER_BLOCK) || world.getBlockState(pos.down()).isOf(Planters.DIRT_PLANTER_BLOCK);
        }
    }
    @Mixin(BigDripleafBlock.class)
    public static class LargeDripleafBlockMixin {
        @ModifyReturnValue(method = "canPlaceAt", at=@At("RETURN"))
        boolean canPlantOnTop(boolean original, BlockState state, WorldView world, BlockPos pos){
            return original || world.getBlockState(pos.down()).isOf(Planters.FARMLAND_PLANTER_BLOCK) || world.getBlockState(pos.down()).isOf(Planters.DIRT_PLANTER_BLOCK);
        }
    }
    @Mixin(BambooShootBlock.class)
    public static class BambooBlockMixin {
        @ModifyReturnValue(method = "canPlaceAt", at=@At("RETURN"))
        boolean canPlantOnTop(boolean original, BlockState state, WorldView world, BlockPos pos){
            return original || world.getBlockState(pos.down()).isOf(Planters.FARMLAND_PLANTER_BLOCK) || world.getBlockState(pos.down()).isOf(Planters.DIRT_PLANTER_BLOCK);
        }
    }
    @Mixin(SeaPickleBlock.class)
    public static class SeaPickleBlockMixin {
        @ModifyReturnValue(method = "canPlaceAt", at=@At("RETURN"))
        boolean canPlantOnTop(boolean original, BlockState state, WorldView world, BlockPos pos){
            return original || world.getBlockState(pos.down()).isOf(Planters.FARMLAND_PLANTER_BLOCK) || world.getBlockState(pos.down()).isOf(Planters.DIRT_PLANTER_BLOCK);
        }
    }
    @Mixin(CropBlock.class)
    public static class CropBlockMixin{
        @ModifyReturnValue(method="canPlantOnTop", at=@At("RETURN"))
        boolean canPlantOnTop(boolean bl1, BlockState floor, BlockView world, BlockPos pos){
            return bl1 || floor.isOf(Planters.FARMLAND_PLANTER_BLOCK);
        }
    }
    @Mixin(PlantBlock.class)
    public static class PlantBlockMixin{
        @ModifyReturnValue(method="canPlantOnTop", at=@At("RETURN"))
        boolean canPlantOnTop(boolean bl1, BlockState floor, BlockView world, BlockPos pos){
            return bl1 || floor.isOf(Planters.FARMLAND_PLANTER_BLOCK) || world.getBlockState(pos.down()).isOf(Planters.DIRT_PLANTER_BLOCK);

        }
    }
}
