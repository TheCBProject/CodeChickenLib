package codechicken.lib.inventory.container;

import codechicken.lib.data.MCDataByteBuf;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * CCL sugared {@link MenuType}.
 * <p>
 * Created by covers1624 on 28/10/19.
 */
public interface CCLMenuType<T extends AbstractContainerMenu> extends IContainerFactory<T> {

    static <T extends AbstractContainerMenu> MenuType<T> create(CCLMenuType<T> factory) {
        return new MenuType<>(factory, FeatureFlags.VANILLA_SET);
    }

    static <T extends AbstractContainerMenu> MenuType<T> create(CCLMenuType<T> factory, FeatureFlag... featureFlags) {
        return new MenuType<>(factory, FeatureFlags.REGISTRY.subset(featureFlags));
    }

    /**
     * Open a menu.
     *
     * @param player   The player.
     * @param provider The menu to open.
     */
    static void openMenu(ServerPlayer player, MenuProvider provider) {
        openMenu(player, provider, null);
    }

    /**
     * Open a menu.
     *
     * @param player   The player.
     * @param provider The menu to open.
     * @param packet   The packet factory for extra data.
     */
    static void openMenu(ServerPlayer player, MenuProvider provider, @Nullable Consumer<MCDataOutput> packet) {
        if (packet != null) {
            player.openMenu(provider, e -> packet.accept(new MCDataByteBuf(e)));
        } else {
            player.openMenu(provider);
        }
    }

    /**
     * Overload of {@link #create(int, Inventory, RegistryFriendlyByteBuf)} using an {@link MCDataInput} instead.
     *
     * @param windowId  The window id.
     * @param inventory The player inventory.
     * @param packet    The packet.
     * @return The new menu.
     */
    T create(int windowId, Inventory inventory, @Nullable MCDataInput packet);

    @Override
    default T create(int windowId, Inventory inv, @Nullable RegistryFriendlyByteBuf data) {
        return create(windowId, inv, data != null ? new MCDataByteBuf(data) : null);
    }
}
