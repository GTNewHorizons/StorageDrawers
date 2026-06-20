package com.jaquadro.minecraft.storagedrawers.util;

public class RenderHelperState {

    public static final int ROTATE0 = 0;
    public static final int ROTATE90 = 1;
    public static final int ROTATE180 = 2;
    public static final int ROTATE270 = 3;

    public static final int TILT_NONE = 0;
    public static final int TILT_DOWN = 1;
    public static final int TILT_UP = 2;

    public static final int[][] ROTATION_BY_FACE_FACE = { { 0, 0, 0, 2, 1, 3 }, { 0, 0, 0, 2, 3, 1 },
            { 0, 0, 0, 2, 3, 1 }, { 0, 0, 2, 0, 1, 3 }, { 0, 0, 1, 3, 0, 2 }, { 0, 0, 3, 1, 2, 0 }, };

    public static final int[][] FACE_BY_FACE_ROTATION = { { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 2, 5, 3, 4 },
            { 3, 4, 2, 5 }, { 4, 2, 5, 3 }, { 5, 3, 4, 2 }, };

    // Face normals indexed by face (YNEG, YPOS, ZNEG, ZPOS, XNEG, XPOS).
    private static final int[][] FACE_NORMAL = { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { -1, 0, 0 },
            { 1, 0, 0 }, };

    private static final int[] IDENTITY = { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
    // Quarter-turn about the Y axis, matching the legacy transformCoord rotation by ROTATE90.
    private static final int[] ROT_Y90 = { 0, 0, -1, 0, 1, 0, 1, 0, 0 };
    // Quarter-turn about the X axis (sends +Z to -Y).
    private static final int[] ROT_X90 = { 1, 0, 0, 0, 0, -1, 0, 1, 0 };

    public double renderMinX;
    public double renderMinY;
    public double renderMinZ;
    public double renderMaxX;
    public double renderMaxY;
    public double renderMaxZ;

    public double renderOffsetX;
    public double renderOffsetY;
    public double renderOffsetZ;

    public boolean flipTexture;
    public boolean renderFromInside;
    public boolean enableAO;

    public int rotateTransform;
    public int tilt;

    private final int[] orient = IDENTITY.clone();
    private final int[] orientInv = IDENTITY.clone();

    public float shiftU;
    public float shiftV;

    public final int[] uvRotate = new int[6];

    public float colorMultYNeg;
    public float colorMultYPos;
    public float colorMultZNeg;
    public float colorMultZPos;
    public float colorMultXNeg;
    public float colorMultXPos;

    public int brightnessTopLeft;
    public int brightnessBottomLeft;
    public int brightnessBottomRight;
    public int brightnessTopRight;

    public final float[] colorTopLeft = new float[3];
    public final float[] colorBottomLeft = new float[3];
    public final float[] colorBottomRight = new float[3];
    public final float[] colorTopRight = new float[3];

    private final double[] scratchIn = new double[3];
    private final double[] scratchOut = new double[3];

    public RenderHelperState() {
        resetColorMult();
    }

    public void setRenderBounds(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        renderMinX = xMin;
        renderMinY = yMin;
        renderMinZ = zMin;
        renderMaxX = xMax;
        renderMaxY = yMax;
        renderMaxZ = zMax;

        if (tilt == TILT_NONE) {
            if (rotateTransform != 0) transformRenderBound(rotateTransform);
        } else {
            transformRenderBoundMatrix();
        }
    }

    public void setRenderOffset(double xOffset, double yOffset, double zOffset) {
        renderOffsetX = xOffset;
        renderOffsetY = yOffset;
        renderOffsetZ = zOffset;

        if (tilt == TILT_NONE) {
            if (rotateTransform != 0) transformRenderOffset(rotateTransform);
        } else {
            transformRenderOffsetMatrix();
        }
    }

    public void clearRenderOffset() {
        renderOffsetX = 0;
        renderOffsetY = 0;
        renderOffsetZ = 0;
    }

    public void setColorMult(float yPos, float z, float x, float yNeg) {
        colorMultYNeg = yNeg;
        colorMultYPos = yPos;
        colorMultZNeg = z;
        colorMultZPos = z;
        colorMultXNeg = x;
        colorMultXPos = x;
    }

    public void resetColorMult() {
        colorMultYNeg = 0.5f;
        colorMultYPos = 1.0f;
        colorMultZNeg = 0.8f;
        colorMultZPos = 0.8f;
        colorMultXNeg = 0.6f;
        colorMultXPos = 0.6f;
    }

    public float getColorMult(int side) {
        return switch (side) {
            case 0 -> colorMultYNeg;
            case 1 -> colorMultYPos;
            case 2 -> colorMultZNeg;
            case 3 -> colorMultZPos;
            case 4 -> colorMultXNeg;
            case 5 -> colorMultXPos;
            default -> 0;
        };
    }

    public void setTextureOffset(float u, float v) {
        shiftU = u;
        shiftV = v;
    }

    public void resetTextureOffset() {
        shiftU = 0;
        shiftV = 0;
    }

    public void setUVRotation(int face, int rotation) {
        uvRotate[face] = rotation;
    }

    public void clearUVRotation(int face) {
        uvRotate[face] = 0;
    }

    public void setColor(float r, float g, float b) {
        colorTopLeft[0] = r;
        colorTopLeft[1] = g;
        colorTopLeft[2] = b;

        colorBottomLeft[0] = r;
        colorBottomLeft[1] = g;
        colorBottomLeft[2] = b;

        colorBottomRight[0] = r;
        colorBottomRight[1] = g;
        colorBottomRight[2] = b;

        colorTopRight[0] = r;
        colorTopRight[1] = g;
        colorTopRight[2] = b;
    }

    public void scaleColor(float[] color, float scale) {
        for (int i = 0; i < color.length; i++) color[i] *= scale;
    }

    public void setRotateTransform(int faceFrom, int faceTo) {
        setOrientation(faceFrom, faceTo, 0);
    }

    /**
     * Configures the geometry transform that maps the canonical drawer (front facing {@code faceFrom}) onto a block
     * whose front points toward {@code direction}, spun by {@code rotation} quarter turns. Horizontal directions use
     * the legacy Y-rotation path unchanged; up/down directions use a general integer rotation matrix.
     */
    public void setOrientation(int faceFrom, int direction, int rotation) {
        if (direction > 1) {
            tilt = TILT_NONE;
            rotateTransform = ROTATION_BY_FACE_FACE[faceFrom][direction];
            if (rotateTransform != 0) {
                transformRenderBound(rotateTransform);
                transformRenderOffset(rotateTransform);
            }
            return;
        }

        tilt = (direction == 1) ? TILT_UP : TILT_DOWN;
        rotateTransform = 0;
        buildOrientation(faceFrom, direction, rotation);
    }

    public void undoRotateTransform() {
        if (tilt == TILT_NONE) {
            if (rotateTransform != 0) {
                transformRenderBound(4 - rotateTransform);
                transformRenderOffset(4 - rotateTransform);
            }
        }
        clearRotateTransform();
    }

    public void clearRotateTransform() {
        rotateTransform = 0;
        tilt = TILT_NONE;
        System.arraycopy(IDENTITY, 0, orient, 0, 9);
        System.arraycopy(IDENTITY, 0, orientInv, 0, 9);
    }

    /**
     * Builds the orientation matrix for a vertical placement: tilt the canonical front to vertical, then spin about the
     * (now vertical) face normal.
     */
    private void buildOrientation(int faceFrom, int direction, int rotation) {
        int[] tiltMatrix;
        if (faceFrom == RenderHelper.ZPOS) tiltMatrix = (direction == 1) ? rotXPow(3) : rotXPow(1);
        else tiltMatrix = (direction == 1) ? rotXPow(1) : rotXPow(3);

        int[] spin = rotYPow(((rotation % 4) + 4) % 4);
        matMul(spin, tiltMatrix, orient);
        transpose(orient, orientInv);

        transformRenderBoundMatrix();
        transformRenderOffsetMatrix();
    }

    /** Maps a canonical face to the world face it occupies under the current orientation. */
    public int mapFace(int face) {
        if (tilt == TILT_NONE) return FACE_BY_FACE_ROTATION[face][rotateTransform];

        int[] n = FACE_NORMAL[face];
        int nx = orient[0] * n[0] + orient[1] * n[1] + orient[2] * n[2];
        int ny = orient[3] * n[0] + orient[4] * n[1] + orient[5] * n[2];
        int nz = orient[6] * n[0] + orient[7] * n[1] + orient[8] * n[2];
        return faceForNormal(nx, ny, nz);
    }

    private static int faceForNormal(int x, int y, int z) {
        if (y < 0) return RenderHelper.YNEG;
        if (y > 0) return RenderHelper.YPOS;
        if (z < 0) return RenderHelper.ZNEG;
        if (z > 0) return RenderHelper.ZPOS;
        if (x < 0) return RenderHelper.XNEG;
        return RenderHelper.XPOS;
    }

    private static int[] rotYPow(int times) {
        int[] result = IDENTITY.clone();
        for (int i = 0; i < times; i++) {
            int[] next = new int[9];
            matMul(ROT_Y90, result, next);
            result = next;
        }
        return result;
    }

    private static int[] rotXPow(int times) {
        int[] result = IDENTITY.clone();
        for (int i = 0; i < times; i++) {
            int[] next = new int[9];
            matMul(ROT_X90, result, next);
            result = next;
        }
        return result;
    }

    private static void matMul(int[] a, int[] b, int[] out) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                out[row * 3 + col] = a[row * 3] * b[col] + a[row * 3 + 1] * b[3 + col] + a[row * 3 + 2] * b[6 + col];
            }
        }
    }

    private static void transpose(int[] in, int[] out) {
        for (int row = 0; row < 3; row++) for (int col = 0; col < 3; col++) out[col * 3 + row] = in[row * 3 + col];
    }

    private void applyOrient(double x, double y, double z, double[] out) {
        double cx = x - 0.5, cy = y - 0.5, cz = z - 0.5;
        out[0] = orient[0] * cx + orient[1] * cy + orient[2] * cz + 0.5;
        out[1] = orient[3] * cx + orient[4] * cy + orient[5] * cz + 0.5;
        out[2] = orient[6] * cx + orient[7] * cy + orient[8] * cz + 0.5;
    }

    private void transformRenderBoundMatrix() {
        applyOrient(renderMinX, renderMinY, renderMinZ, scratchOut);
        double aX = scratchOut[0], aY = scratchOut[1], aZ = scratchOut[2];
        applyOrient(renderMaxX, renderMaxY, renderMaxZ, scratchOut);
        double bX = scratchOut[0], bY = scratchOut[1], bZ = scratchOut[2];

        renderMinX = Math.min(aX, bX);
        renderMaxX = Math.max(aX, bX);
        renderMinY = Math.min(aY, bY);
        renderMaxY = Math.max(aY, bY);
        renderMinZ = Math.min(aZ, bZ);
        renderMaxZ = Math.max(aZ, bZ);
    }

    private void transformRenderOffsetMatrix() {
        double ox = renderOffsetX, oy = renderOffsetY, oz = renderOffsetZ;
        renderOffsetX = orient[0] * ox + orient[1] * oy + orient[2] * oz;
        renderOffsetY = orient[3] * ox + orient[4] * oy + orient[5] * oz;
        renderOffsetZ = orient[6] * ox + orient[7] * oy + orient[8] * oz;
    }

    private void transformRenderOffset(int rotation) {
        double scratch;
        switch (rotation) {
            case ROTATE90:
                scratch = renderOffsetX;
                renderOffsetX = -renderOffsetZ;
                renderOffsetZ = scratch;
                break;
            case ROTATE180:
                renderOffsetX = -renderOffsetX;
                renderOffsetZ = -renderOffsetZ;
                break;
            case ROTATE270:
                scratch = renderOffsetX;
                renderOffsetX = renderOffsetZ;
                renderOffsetZ = -scratch;
                break;
        }
    }

    private void transformRenderBound(int rotation) {
        scratchIn[0] = renderMinX;
        scratchIn[1] = renderMinY;
        scratchIn[2] = renderMinZ;
        transformCoord(scratchIn, scratchOut, rotation);
        renderMinX = scratchOut[0];
        renderMinY = scratchOut[1];
        renderMinZ = scratchOut[2];

        scratchIn[0] = renderMaxX;
        scratchIn[1] = renderMaxY;
        scratchIn[2] = renderMaxZ;
        transformCoord(scratchIn, scratchOut, rotation);
        renderMaxX = scratchOut[0];
        renderMaxY = scratchOut[1];
        renderMaxZ = scratchOut[2];

        if (renderMinX > renderMaxX) {
            double temp = renderMinX;
            renderMinX = renderMaxX;
            renderMaxX = temp;
        }

        if (renderMinZ > renderMaxZ) {
            double temp = renderMinZ;
            renderMinZ = renderMaxZ;
            renderMaxZ = temp;
        }
    }

    public void transformCoord(double x, double y, double z, double[] coordCout, int rotation) {
        scratchIn[0] = x;
        scratchIn[1] = y;
        scratchIn[2] = z;
        transformCoord(scratchIn, coordCout, rotation);
    }

    public void transformCoord(double[] coordIn, double[] coordOut, int rotation) {
        coordOut[1] = coordIn[1];

        switch (rotation) {
            case 1:
                coordOut[0] = 1 - coordIn[2];
                coordOut[2] = coordIn[0];
                break;
            case 2:
                coordOut[0] = 1 - coordIn[0];
                coordOut[2] = 1 - coordIn[2];
                break;
            case 3:
                coordOut[0] = coordIn[2];
                coordOut[2] = 1 - coordIn[0];
                break;
            case 0:
            default:
                coordOut[0] = coordIn[0];
                coordOut[2] = coordIn[2];
                break;
        }
    }
}
