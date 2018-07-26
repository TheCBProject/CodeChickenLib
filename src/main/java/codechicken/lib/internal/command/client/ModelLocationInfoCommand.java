package codechicken.lib.internal.command.client;

import codechicken.lib.command.ClientCommandBase;
import codechicken.lib.command.help.IBetterHelpCommand;
import codechicken.lib.render.item.CCRenderItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.command.CommandException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by covers1624 on 9/06/18.
 */
@SideOnly (Side.CLIENT)
public class ModelLocationInfoCommand extends ClientCommandBase implements IBetterHelpCommand {

    @Override
    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) {
            stack = player.getHeldItemOffhand();
            if (stack.isEmpty()) {
                player.sendMessage(new TextComponentString("You do not appear to be holding anything."));
                return;
            }
        }
        Item item = stack.getItem();
        String itemLoc = CCRenderItem.getModelForStack(stack).toString();
        player.sendMessage(new TextComponentString(YELLOW + "ItemModel: " + RESET + itemLoc));
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            BlockStateMapper stateMapper = mc.getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper();
            player.sendMessage(new TextComponentString(BLUE + "IBlockState assignments" + RESET + ":"));
            for (Entry<IBlockState, ModelResourceLocation> entry : stateMapper.getVariants(block).entrySet()) {
                player.sendMessage(new TextComponentString(YELLOW + " " + entry.getKey()));
                player.sendMessage(new TextComponentString(AQUA + "  " + entry.getValue()));
            }
        }

    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getDesc() {
        return "Dumps model info about the item you are holding.";
    }

    @Override
    public List<String> getHelp() {
        List<String> lines = new ArrayList<>();
        lines.add("Syntax: '/ccl model_loc_info'");
        lines.add("The command will the held item's current ModelResourceLocation being used.");
        lines.add("If the held item is an ItemBlock, it will dump all IBlockState <-> ModelResourceLocation assignments.");
        return lines;
    }

    @Override
    public String getName() {
        return "model_info";
    }
}
