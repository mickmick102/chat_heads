package dzwdz.chat_heads.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.config.Configs;
import org.figuramc.figura.model.rendering.AvatarRenderer;
import org.figuramc.figura.model.rendering.PartFilterScheme;
import org.figuramc.figura.utils.ui.UIHelper;
import org.joml.Vector3f;

public class FiguraPortraitRenderer {

    public static boolean renderPortrait(Avatar avatar, GuiGraphics gui, int x, int y, int size, float modelScale, boolean upsideDown) {
        AvatarRenderer renderer = avatar.renderer;
        if (!Configs.AVATAR_PORTRAIT.value || renderer == null || !avatar.loaded)
            return false;

        // matrices
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(x, y, 0d);
        pose.scale(modelScale, modelScale * (upsideDown ? 1 : -1), modelScale);
        pose.mulPose(Axis.XP.rotationDegrees(180f));

        // scissors
        Vector3f pos = pose.last().pose().transformPosition(new Vector3f());

        int x1 = (int) pos.x;
        int y1 = (int) pos.y;
        int x2 = (int) pos.x + size;
        int y2 = (int) pos.y + size;

        gui.enableScissor(x1, y1, x2, y2);
        UIHelper.paperdoll = true;
        UIHelper.dollScale = 16f;

        // setup render
        pose.translate(4d / 16d, upsideDown ? 0 : (8d / 16d), 0d);

        Lighting.setupForFlatItems();

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        int light = LightTexture.FULL_BRIGHT;

        renderer.allowPivotParts = false;

        renderer.setupRenderer(
                PartFilterScheme.PORTRAIT, buffer, pose,
                1f, light, 1f, OverlayTexture.NO_OVERLAY,
                false, false
        );

        // render
        int comp = renderer.renderSpecialParts();
        boolean ret = comp > 0;

        // after render
        buffer.endBatch();
        pose.popPose();

        gui.disableScissor();
        UIHelper.paperdoll = false;

        renderer.allowPivotParts = true;

        // return
        return ret;
    }
}
