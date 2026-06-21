package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

/**
 * Resolved placement orientation of a drawer block.
 *
 * <p>
 * {@code direction} is a {@link net.minecraftforge.common.util.ForgeDirection} ordinal (0-5) describing the face the
 * drawer front points toward. {@code rotation} is the horizontal spin (0-3, quarter turns) applied around that face; it
 * is only meaningful when the front points up or down, and stays 0 for horizontal placements so their saved data and
 * rendering remain unchanged.
 */
public class DrawerOrientation {

    /** Minimum pitch magnitude in degrees required to place facing up or down. */
    private static final float VERTICAL_PITCH_THRESHOLD = 65f;

    private final int direction;
    private final int rotation;

    public DrawerOrientation(int direction, int rotation) {
        this.direction = direction;
        this.rotation = rotation;
    }

    public int direction() {
        return direction;
    }

    public int rotation() {
        return rotation;
    }

    /**
     * Computes the orientation a drawer should take when placed by the given entity, based entirely on where the entity
     * is looking rather than which block face was clicked.
     */
    public static DrawerOrientation forEntity(EntityLivingBase entity) {
        int quadrant = MathHelper.floor_double((entity.rotationYaw * 4f / 360f) + 0.5) & 3;
        int horizontal = switch (quadrant) {
            case 0 -> 2;
            case 1 -> 5;
            case 2 -> 3;
            default -> 4;
        };

        float pitch = entity.rotationPitch;
        // Looking up places the drawer overhead with its front pointing back down at the player; looking down places it
        // underfoot facing up. The spin is chosen so the front's bottom edge faces the player (matching how a
        // horizontal drawer's bottom points down), derived from the rigid render tilt.
        if (pitch < -VERTICAL_PITCH_THRESHOLD) return new DrawerOrientation(0, (4 - quadrant) % 4);
        if (pitch > VERTICAL_PITCH_THRESHOLD) return new DrawerOrientation(1, (2 - quadrant + 4) % 4);

        return new DrawerOrientation(horizontal, 0);
    }
}
