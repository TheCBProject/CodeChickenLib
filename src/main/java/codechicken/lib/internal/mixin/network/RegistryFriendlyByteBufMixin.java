package codechicken.lib.internal.mixin.network;

import codechicken.lib.packet.CCRegistryFriendlyByteBufExtension;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Created by covers1624 on 7/25/26.
 */
@Mixin (RegistryFriendlyByteBuf.class)
public class RegistryFriendlyByteBufMixin implements CCRegistryFriendlyByteBufExtension {
}
