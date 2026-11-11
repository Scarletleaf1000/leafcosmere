/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package leaf.cosmere.hemalurgy.client.render.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.Set;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointState;
import leaf.cosmere.hemalurgy.common.BindPointGeometry;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class BindPointTable {
    public static final Set<String> BACK_KEYS = BindPointState.BACK_KEYS;

    private BindPointTable() {
    }

    public static Set<String> keys() {
        return BindPointGeometry.keys();
    }

    public static boolean isBackKey(String key) {
        return BACK_KEYS.contains(key);
    }

    public static float[] screenPosition(String key, boolean back, int centreX, int anchorY, float k) {
        BindPointGeometry.CubeDef def = BindPointGeometry.cube(key);
        if (def == null) {
            return null;
        }
        float[] pivot = BindPointGeometry.pivot(def.group());
        if (pivot == null) {
            return null;
        }
        float scale = k * 64.0f;
        Vector3f v = new Vector3f(def.x() / 16.0f, def.y() / 16.0f, def.z() / 16.0f);
        v.add(pivot[0] / 16.0f, pivot[1] / 16.0f, pivot[2] / 16.0f);
        v.add(0.0f, -1.501f, 0.0f);
        v.mul(0.9375f);
        v.mul(-1.0f, -1.0f, 1.0f);
        Quaternionf pose = back ? new Quaternionf().rotateZ((float)Math.PI).mul((Quaternionfc)Axis.YP.rotationDegrees(180.0f)) : new Quaternionf().rotateZ((float)Math.PI);
        new Matrix4f().rotate((Quaternionfc)pose).transformPosition(v);
        v.mul(scale, scale, -scale);
        v.add((float)centreX, (float)anchorY, 50.0f);
        Vector3f ndc = RenderSystem.getProjectionMatrix().transformPosition(v);
        Minecraft mc = Minecraft.getInstance();
        float guiW = mc.getWindow().getGuiScaledWidth();
        float guiH = mc.getWindow().getGuiScaledHeight();
        float px = (ndc.x + 1.0f) * 0.5f * guiW;
        float py = (1.0f - ndc.y) * 0.5f * guiH;
        return new float[]{px, py};
    }
}
