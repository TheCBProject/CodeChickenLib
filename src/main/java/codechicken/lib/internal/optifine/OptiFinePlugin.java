package codechicken.lib.internal.optifine;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class OptiFinePlugin implements IMixinConfigPlugin {

    private boolean shouldApplyMixin;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("optifine.OptiFineTransformationService", false, Thread.currentThread().getContextClassLoader());
            this.shouldApplyMixin = true;
        } catch (Throwable throwable) {
            // no-op
        }
    }

    //@formatter:off
    @Override public String getRefMapperConfig() { return null; }
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return shouldApplyMixin; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    //@formatter:on
}