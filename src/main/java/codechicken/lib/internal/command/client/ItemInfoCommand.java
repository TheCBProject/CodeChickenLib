package codechicken.lib.internal.command.client;

import codechicken.lib.command.ClientCommandBase;
import codechicken.lib.command.help.IBetterHelpCommand;
import codechicken.lib.util.ArrayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 9/06/18.
 */
@SideOnly (Side.CLIENT)
public class ItemInfoCommand extends ClientCommandBase implements IBetterHelpCommand {

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
        boolean nbtToClipboard = args.length >= 1 && Boolean.parseBoolean(args[1]);
        String nbt = stack.hasTagCompound() ? stack.getTagCompound().toString() : "Item has no NBT.";

        List<String> ores = ArrayUtils.toList(OreDictionary.getOreIDs(stack)).stream()//
                .map(OreDictionary::getOreName)//
                .collect(Collectors.toList());
        player.sendMessage(new TextComponentString("RegistryName: " + stack.getItem().getRegistryName()));
        player.sendMessage(new TextComponentString("Meta: " + stack.getMetadata()));
        player.sendMessage(new TextComponentString("OreDictionary:"));
        for (String ore : ores) {
            player.sendMessage(new TextComponentString("  " + ore));
        }
        player.sendMessage(new TextComponentString("NBT: " + nbt));
        if (nbtToClipboard && stack.hasTagCompound()) {
            StringSelection sel = new StringSelection(nbt);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
            player.sendMessage(new TextComponentString("NBT Copied to clipboard!"));
        }
    }

    @Override
    public String getName() {
        return "item_info";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getDesc() {
        return "Dumps some info on the item you are holding.";
    }

    @Override
    public List<String> getHelp() {
        List<String> lines = new ArrayList<>();
        lines.add("{} Defines optional choice parameters.");
        lines.add("Syntax: '/ccl item_info {true|false}");
        lines.add("Specifying true as the parameter will copy the NBT of the item to your clipboard.");
        lines.add("If you don't specify the parameter it will default to false.");
        lines.add("The command will attempt to use the item in your MainHand, failing that it will attempt your OffHand.");
        return lines;
    }
}
