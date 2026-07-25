package codechicken.lib.internal.mixin.network;

import codechicken.lib.packet.CCFriendlyByteBufExtension;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Created by covers1624 on 7/25/26.
 */
@Mixin (FriendlyByteBuf.class)
public class FriendlyByteBufMixin implements CCFriendlyByteBufExtension {
}
