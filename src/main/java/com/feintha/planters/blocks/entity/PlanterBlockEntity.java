package com.feintha.planters.blocks.entity;

import com.feintha.planters.Planters;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PlanterBlockEntity extends BlockEntity {
    public PlanterBlockEntity(BlockPos pos, BlockState state) {
        super(Planters.PLANTER_BLOCK_ENTITY, pos, state);
        if (world != null) {
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public Text NAME_TAG = Text.empty();
    public ItemStack BANNER = ItemStack.EMPTY;
    public Text signText = Text.empty();
    public boolean hasNameTag = false;
    public float BannerRotation = 0f;
    public boolean hasText() {
        return !signText.getLiteralString().isEmpty();
    }
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("hasNameTag", hasNameTag);
        nbt.putString("name", Text.Serialization.toJsonString(NAME_TAG));
        nbt.put("banner", BANNER.writeNbt(new NbtCompound()));
        nbt.putFloat("rotation", BannerRotation);
        nbt.putString("sign_data", Text.Serialization.toJsonString(signText));
    }
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        hasNameTag = nbt.getBoolean("hasNameTag");
        NAME_TAG = Text.Serialization.fromJson(nbt.getString("name"));
        BANNER = ItemStack.fromNbt(nbt.getCompound("banner"));
        BannerRotation = nbt.getFloat("rotation");
        signText = Text.Serialization.fromJson(nbt.getString("sign_data"));
    }
}
