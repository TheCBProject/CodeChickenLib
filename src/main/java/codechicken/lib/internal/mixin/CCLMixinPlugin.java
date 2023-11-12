package codechicken.lib.internal.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Created by covers1624 on 13/11/23.
 */
public class CCLMixinPlugin implements IMixinConfigPlugin {

    private static final boolean USE_DEV_MIXINS = Boolean.getBoolean("ccl.dev.mixins");

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Don't load development mixins unless explicitly enabled.
        // These mixins are usually quite hacky/aggressive and accomplish specific things for my dev environment.
        return !mixinClassName.startsWith("codechicken.lib.internal.mixin.dev") || USE_DEV_MIXINS;
    }

    // @formatter:off
    @Override public void onLoad(String mixinPackage) { }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    // @formatter:on
}
