package codechicken.lib.internal.optifine.mixin;

import codechicken.lib.internal.optifine.OptiFineGameSettingsBridge;
import net.minecraft.client.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin (GameSettings.class)
public abstract class GameSettingsMixin implements OptiFineGameSettingsBridge {

    @SuppressWarnings ({"ShadowTarget", "target"})
    @Shadow (remap = false)
    private boolean ofFastRender;

    @Override
    public boolean bridge$isFastRender() {
        return ofFastRender;
    }

    @Override
    public void bridge$setFastRender(boolean value) {
        this.ofFastRender = value;
    }
}