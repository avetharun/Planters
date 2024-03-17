package com.feintha.planters.blocks.entity.renderer;

import com.feintha.planters.blocks.entity.PlanterBlockEntity;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.RandomSeed;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.Random;

public class PlanterBlockEntityRenderer implements BlockEntityRenderer<PlanterBlockEntity> {
    public PlanterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -6.0F, 7.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
                .uv(2, 2).cuboid(-9.0F, -12.0F, 2.0F, 2.0F, 6.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    ModelPart signPart = getTexturedModelData().createModel();
    @Override
    public void render(PlanterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.hasText()){
            matrices.push();
            matrices.translate(0.5, 1.625, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.BannerRotation));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.translate(0, 0, 0.4);
            matrices.scale(0.45f,0.45f,0.45f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            signPart.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(new Identifier("planters:textures/block/planter_sign.png"))), light, overlay);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            matrices.translate(0,0.76,-0.07);
            matrices.scale(0.0095f,0.0095f,0.0095f);
            var a = MinecraftClient.getInstance().textRenderer.wrapLines(entity.signText,70);
            int _i = 0;
            for (OrderedText t : a) {
                if (_i > 3) {break;}
                MinecraftClient.getInstance().textRenderer.draw(a.get(_i), ((float) -MinecraftClient.getInstance().textRenderer.getWidth(a.get(_i)) / 2), 0f, 0xffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                matrices.translate(0,10,0);
                _i++;
            }
            if (a.size() == 2) {

            }
            matrices.pop();
        }
        if (entity.BANNER != null && !entity.BANNER.isEmpty()) {
            var b = entity.BANNER;
            matrices.push();
            long seed =entity.getPos().asLong();
            seed ^= entity.BANNER.writeNbt(new NbtCompound()).toString().hashCode();
            Random r = new Random(seed);
            float ox = r.nextFloat(-8,8) / 20;
            float oz = r.nextFloat(-8,8) / 20;
            matrices.translate(0.5, 1, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-entity.BannerRotation * MathHelper.RADIANS_PER_DEGREE));
            matrices.translate(0, 0, 0.4);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.BANNER, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
        if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult bhr) {
            assert MinecraftClient.getInstance().player != null;
            var e1 = MinecraftClient.getInstance().player.clientWorld.getBlockEntity(bhr.getBlockPos());
            var p1 = entity.getPos();
            var p2 = bhr.getBlockPos();
            int yOff = 0;
            if (e1 instanceof PlanterBlockEntity e && p1.getX() == p2.getX() && p1.getY() == p2.getY() && p1.getZ() == p2.getZ()) {
//                while (true) {
//                    assert MinecraftClient.getInstance().world != null;
//                    if ((yOff > MinecraftClient.getInstance().world.getHeight())) break;
//                    if (entity.getWorld().getBlockState(entity.getPos().add(0, yOff, 0)).isAir()) break;
//                    yOff++;
//                }
                if (e.hasNameTag) {
                    var text = e.NAME_TAG;
                    matrices.push();
                    matrices.translate(0.5F, 1.25 + (entity.getWorld().getBlockState(entity.getPos().up()).isAir() ? 0 : 0.75), 0.5F);
                    matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
                    matrices.scale(-0.025F, -0.025F, 0.025F);
                    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                    float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
                    int j = (int) (g * 255.0F) << 24;
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    float h = (float) (-textRenderer.getWidth(text) / 2);
                    textRenderer.draw(text, h, 0, 0xffffff, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, j, light);
                    matrices.pop();
                }
            }
        }
    }
}
