package com.jaquadro.minecraft.storagedrawers.integration;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.relauncher.Side;

public abstract class IntegrationModule {

    @NotNull
    public abstract String getModID();

    protected abstract boolean moduleConfig();

    protected String versionPattern() {
        return null;
    }

    protected LoadingSide loadingSide() {
        return LoadingSide.BOTH;
    }

    public void init() throws Throwable {}

    public void postInit() {}

    public final boolean shouldLoadModule() {
        return moduleConfig() && checkVersion() && checkSide();
    }

    private boolean checkVersion() {
        String pattern = versionPattern();
        if (pattern == null) return true;
        VersionRange validVersions;
        try {
            validVersions = VersionRange.createFromVersionSpec(pattern);
        } catch (InvalidVersionSpecificationException e) {
            return false;
        }
        for (ModContainer mod : Loader.instance().getModList()) {
            if (mod.getModId().equals(getModID())) {
                ArtifactVersion version = new DefaultArtifactVersion(mod.getVersion());
                return validVersions.containsVersion(version);
            }
        }
        return false;
    }

    private boolean checkSide() {
        if (loadingSide() == LoadingSide.BOTH) {
            return true;
        }
        Side side = FMLCommonHandler.instance().getSide();
        if (side == Side.CLIENT && loadingSide() == LoadingSide.CLIENT) return true;
        return side == Side.SERVER && loadingSide() == LoadingSide.SERVER;
    }

    protected enum LoadingSide {
        CLIENT,
        SERVER,
        BOTH
    }
}
