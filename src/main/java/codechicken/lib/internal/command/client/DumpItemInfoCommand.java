package codechicken.lib.internal.command.client;

import codechicken.lib.util.ArrayUtils;
import com.google.common.base.Joiner;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 18/10/2017.
 */
public class DumpItemInfoCommand implements ICommand {

    @Override
    public String getName() {
        return "ccl_item_info";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Dumps info on the item.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        boolean nbtToClipboard = args.length == 1;
        String nbt = stack.hasTagCompound() ? stack.getTagCompound().toString() : "null";
        sender.sendMessage(new TextComponentString("RegistryName: " + stack.getItem().getRegistryName()));
        sender.sendMessage(new TextComponentString("Metadata: " + stack.getMetadata()));

        List<String> s = ArrayUtils.toList(OreDictionary.getOreIDs(stack)).stream().map(OreDictionary::getOreName).collect(Collectors.toList());
        sender.sendMessage(new TextComponentString("OreDictionary: " + Joiner.on(", ").join(s)));
        sender.sendMessage(new TextComponentString("NBT: " + nbt));
        if (nbtToClipboard && stack.hasTagCompound()) {
            StringSelection sel = new StringSelection(nbt);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
            sender.sendMessage(new TextComponentString("NBT Copied to clipboard."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
