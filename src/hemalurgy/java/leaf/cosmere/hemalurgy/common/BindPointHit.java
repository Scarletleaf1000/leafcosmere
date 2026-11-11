/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common;

import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Shared look test for bind points: finds which bind-point cube on a target entity
 * the ray from the eye along the look direction passes through.
 */
public final class BindPointHit
{
	private static final double MAX_DISTANCE = 4.5;
	private static final double HIT_RADIUS = 2.5 / 16.0;

	private BindPointHit()
	{
	}

	/**
	 * Returns the bind-point key whose world-space centre is hit by the ray from eye
	 * along look, within {@value #MAX_DISTANCE} blocks, or null.
	 *
	 * @param partialTicks interpolation factor matching the renderer (1.0 on the server)
	 */
	public static String findHit(LivingEntity target, Vec3 eye, Vec3 look, float partialTicks)
	{
		KeyHit hit = findKeyHit(target, eye, look, partialTicks);
		return hit == null ? null : hit.key();
	}

	private static KeyHit findKeyHit(LivingEntity target, Vec3 eye, Vec3 look, float partialTicks)
	{
		KeyHit best = null;
		for (String key : BindPointGeometry.keys())
		{
			Vec3 centre = worldCentre(target, key, partialTicks);
			Vec3 toCentre = centre.subtract(eye);
			double t = toCentre.dot(look);
			if (t <= 0.0 || t > MAX_DISTANCE)
			{
				continue;
			}
			Vec3 closest = eye.add(look.scale(t));
			if (closest.distanceTo(centre) <= HIT_RADIUS && (best == null || t < best.t()))
			{
				best = new KeyHit(key, t);
			}
		}
		return best;
	}

	/**
	 * World-space centre of a bind-point cube. Mirrors the renderer transform chain:
	 * EntityRenderDispatcher.render (translate to entity position),
	 * LivingEntityRenderer.setupRotations (YP.rotationDegrees(180 - yBodyRot)),
	 * LivingEntityRenderer.render (scale(-1,-1,1), empty scale(), translate(0,-1.501,0))
	 * and ModelPart.translateAndRotate (part pivot, rest pose).
	 */
	private static Vec3 worldCentre(LivingEntity target, String key, float partialTicks)
	{
		BindPointGeometry.CubeDef def = BindPointGeometry.cube(key);
		float[] pivot = BindPointGeometry.pivot(def.group());

		// part-local cube centre + part pivot, in 1/16 units (ModelPart.translateAndRotate, rest pose)
		double x = (pivot[0] + def.x()) / 16.0;
		double y = (pivot[1] + def.y()) / 16.0;
		double z = (pivot[2] + def.z()) / 16.0;

		// LivingEntityRenderer.render: poseStack.translate(0.0F, -1.501F, 0.0F)
		y -= 1.501;

		// LivingEntityRenderer.render: poseStack.scale(-1.0F, -1.0F, 1.0F)
		x = -x;
		y = -y;

		// LivingEntityRenderer.setupRotations: Axis.YP.rotationDegrees(180.0F - lerped yBodyRot)
		float yBodyRot = Mth.rotLerp(partialTicks, target.yBodyRotO, target.yBodyRot);
		Vector3f v = new Vector3f((float) x, (float) y, (float) z);
		v.rotate(Axis.YP.rotationDegrees(180.0F - yBodyRot));

		// EntityRenderDispatcher.render: poseStack.translate(interpolated entity position)
		double px = Mth.lerp(partialTicks, target.xo, target.getX());
		double py = Mth.lerp(partialTicks, target.yo, target.getY());
		double pz = Mth.lerp(partialTicks, target.zo, target.getZ());
		return new Vec3(v.x + px, v.y + py, v.z + pz);
	}

	private record KeyHit(String key, double t)
	{
	}
}
