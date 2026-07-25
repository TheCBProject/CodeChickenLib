package codechicken.lib.gui.modular;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A supplier for sprites.
 * <p>
 * May return null to indicate the sprite is not available. This will usually disable
 * the thing being rendered.
 * <p>
 * Created by covers1624 on 4/1/26.
 */
@FunctionalInterface
public interface SpriteSupplier extends Supplier<TextureAtlasSprite> {

    SpriteSupplier EMPTY = () -> null;

    @Override
    @Nullable
    TextureAtlasSprite get();

    default void ifPresent(Consumer<TextureAtlasSprite> cons) {
        var tex = get();
        if (tex != null) {
            cons.accept(tex);
        }
    }

    static SpriteSupplier gui(Identifier spriteLoc) {
        return of(AtlasIds.GUI, spriteLoc);
    }

    static SpriteSupplier of(Identifier atlasLoc, Identifier spriteLoc) {
        var memo = Suppliers.memoize(() -> Minecraft.getInstance().getAtlasManager().get(new Material(atlasLoc, spriteLoc)));
        return memo::get;
    }

    static SpriteSupplier gui(Supplier<Identifier> sup) {
        return of(() -> new Material(AtlasIds.GUI, sup.get()));
    }

    static SpriteSupplier of(Supplier<Material> sup) {
        var atlasManager = Minecraft.getInstance().getAtlasManager();
        return () -> atlasManager.get(sup.get());
    }
}
