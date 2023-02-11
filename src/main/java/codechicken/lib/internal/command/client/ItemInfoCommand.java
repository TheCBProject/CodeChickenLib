package codechicken.lib.internal.command.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 2/9/22.
 */
public class ItemInfoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("item_info")
                        .executes(ItemInfoCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> command) {
        CommandSourceStack ctx = command.getSource();
        Player player = command.getSource().getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            stack = player.getOffhandItem();
        }
        if (stack.isEmpty()) return 0;

        String tag = stack.hasTag() ? stack.getTag().toString() : "Item has no NBT.";
        ctx.sendSuccess(Component.literal("Registry Name: " + ForgeRegistries.ITEMS.getKey(stack.getItem())), false);
        ctx.sendSuccess(Component.literal("NBT          : " + tag), false);
        return 0;
    }
}
