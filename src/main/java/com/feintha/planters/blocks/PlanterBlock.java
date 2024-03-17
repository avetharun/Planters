package com.feintha.planters.blocks;

import com.feintha.planters.Planters;
import com.feintha.planters.blocks.entity.PlanterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

@SuppressWarnings("deprecation")
public class PlanterBlock extends BlockWithEntity {

    public static final DirectionProperty FACING;
    public static final EnumProperty<StairShape> SHAPE;

    public PlanterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, StairShape.STRAIGHT).with(FACING, Direction.NORTH));
    }
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return BlockWithEntity.createCodec(PlanterBlock::new);
    }
//    public void update(BlockState state, World world, BlockPos pos, BlockState neighborState, BlockPos neighborPos, Direction direction, Text name) {
//        if (!neighborState.isAir() && neighborState.getBlock() instanceof PlanterBlock && state.getBlock() instanceof PlanterBlock) {
//            if (neighborState.get(FACING) != direction || state.get(FACING) == Direction.UP) {
//                if (world.getBlockEntity(pos) instanceof PlanterBlockEntity pbe1) {
//                    pbe1.NAME_TAG = name;
//                    pbe1.hasNameTag = true;
//                    for (int z = -1; z <= 1; z++) {
//                        for (int x = -1; x <= 1; x++) {
//                            var nPs = pos.mutableCopy().add(x,0,z);
//                            BlockState neighborState1 = world.getBlockState(nPs);
//                            if (neighborState1.getBlock() instanceof PlanterBlock && world.getBlockEntity(nPs) instanceof PlanterBlockEntity pbe && !pbe.hasNameTag) {
//                                Direction d = Direction.UP;
//                                BlockPos dPos = pos.subtract(nPs);
//                                var xA = nPs.getComponentAlongAxis(Direction.Axis.X);
//                                var zA = nPs.getComponentAlongAxis(Direction.Axis.Z);
//                                if (x == -1 || x == 1 && z == 0) {
//                                    d = zA == -1 ? Direction.WEST : Direction.EAST;
//                                }
//                                if (z == -1 || z == 1 && x == 0) {
//                                    d = zA == -1 ? Direction.NORTH : Direction.SOUTH;
//                                }
//                                if (d == Direction.UP) {
//                                    continue;
//                                }
//                                update(neighborState1, world, nPs, state, pos, d, name);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof PlanterBlockEntity pbe) {
            boolean rotatable_stack = false;
            boolean returns = false;
            if (player.getStackInHand(hand).isEmpty() && player.isSneaking()) {
                pbe.BANNER = ItemStack.EMPTY;
                pbe.hasNameTag = false;
                pbe.signText = Text.empty();
                return ActionResult.CONSUME;
            }
            if (player.getStackInHand(hand).isOf(Items.NAME_TAG)) {
                pbe.NAME_TAG = player.getStackInHand(hand).getName().copy().formatted(Formatting.RESET);
                pbe.hasNameTag = true;
                return ActionResult.CONSUME;
            }
            if (player.getStackInHand(hand).isIn(ItemTags.BANNERS)) {
                pbe.BANNER = player.getStackInHand(hand).copyWithCount(1);
                if (!pbe.signText.equals(Text.empty())) {
                    pbe.signText = Text.empty();
                }
                pbe.BannerRotation = (Math.round(player.getYaw() / 10f) * 10) + 180f;
                return ActionResult.CONSUME;
            }
            if (player.getStackInHand(hand).isIn(ItemTags.SIGNS)) {
                if (!pbe.BANNER.isEmpty()) {
                    pbe.BANNER = ItemStack.EMPTY;
                }
                pbe.signText = player.getStackInHand(hand).getName();
                pbe.BannerRotation = (Math.round(player.getYaw() / 10f) * 10) + 180f;
                return ActionResult.CONSUME;
            }
        }
        if (state.isOf(Planters.DIRT_PLANTER_BLOCK)) {
            if (player.getStackInHand(hand).isIn(ItemTags.HOES)) {
                world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS);
                if (!world.isClient) {
                    BlockState newState = Planters.FARMLAND_PLANTER_BLOCK.getStateWithProperties(state);
                    player.getStackInHand(hand).damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                    world.setBlockState(pos, newState, 11);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                }
                player.swingHand(hand);
                return ActionResult.CONSUME;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0,0,0,1,0.995,1);
    }

    private int getShapeIndexIndex(BlockState state) {
        return state.get(SHAPE).ordinal() * 4 + state.get(FACING).getHorizontal();
    }

    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        BlockState block = world.getBlockState(pos.up());
        if (!block.isAir()) {
            block.getBlock().onBroken(world, pos.up(), state);
        }
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
        var world = ctx.getWorld();
        if (ctx.getPlayer().isSneaking()) {
            return blockState.with(Properties.FACING, Direction.UP);
        }
        return blockState.with(SHAPE, getStairShape(blockState, ctx.getWorld(), blockPos));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        return direction.getAxis().isHorizontal() ? state.with(SHAPE, getStairShape(state, world, pos)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }




    private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction = (Direction)state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        BlockState blockState_2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (blockState_2.getBlock() instanceof PlanterBlock && blockState_2.get(FACING) == Direction.UP) {
            return StairShape.STRAIGHT;
        }
        if (isStairs(blockState)) {
            Direction direction2 = (Direction)blockState.get(FACING);
            if (direction2.getAxis() != ((Direction)state.get(FACING)).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return StairShape.OUTER_LEFT;
                }

                return StairShape.OUTER_RIGHT;
            }
        }

        BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (isStairs(blockState2)) {
            Direction direction3 = (Direction)blockState2.get(FACING);
            if (direction3.getAxis() != ((Direction)state.get(FACING)).getAxis() && isDifferentOrientation(state, world, pos, direction3)) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    return StairShape.INNER_LEFT;
                }

                return StairShape.INNER_RIGHT;
            }
        }

        return StairShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !isStairs(blockState) || blockState.get(FACING) != state.get(FACING);
    }

    public static boolean isStairs(BlockState state) {
        return state.getBlock() instanceof PlanterBlock;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction direction = state.get(FACING);
        StairShape stairShape = state.get(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    return switch (stairShape) {
                        case INNER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                        case INNER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                        case OUTER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                        default -> state.rotate(BlockRotation.CLOCKWISE_180);
                    };
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    return switch (stairShape) {
                        case INNER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                        case INNER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                        case OUTER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                        case STRAIGHT -> state.rotate(BlockRotation.CLOCKWISE_180);
                    };
                }
        }

        return super.mirror(state, mirror);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    static {
        FACING = Properties.FACING;
        SHAPE = Properties.STAIR_SHAPE;
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlanterBlockEntity(pos, state);
    }
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
