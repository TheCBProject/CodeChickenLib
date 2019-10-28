//package codechicken.lib.internal.command.client.highlight;
//
//import codechicken.lib.command.ClientCommandBase;
//import codechicken.lib.command.help.IBetterHelpCommand;
//import codechicken.lib.internal.HighlightHandler;
//import net.minecraft.block.Block;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.command.CommandException;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.TextComponentString;
//
//import java.util.Collections;
//import java.util.List;
//
//import static codechicken.lib.util.LambdaUtils.tryOrNull;
//
///**
// * Created by covers1624 on 17/01/19.
// */
//public class HighlightInfoCommand extends ClientCommandBase implements IBetterHelpCommand {
//
//    @Override
//    @SuppressWarnings ("Convert2MethodRef")
//    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
//        BlockPos pos = HighlightHandler.highlight;
//        if(pos == null) {
//            throw new CommandException("Highlight not enabled.");
//        }
//        IBlockState state = player.world.getBlockState(pos);
//        Block block = state.getBlock();
//        TileEntity tile = player.world.getTileEntity(pos);
//        StringBuilder builder = new StringBuilder("\n Block info:\n");
//        builder.append("  BlockPos:      ").append(String.format("x:%s, y:%s, z:%s", pos.getX(), pos.getY(), pos.getZ())).append("\n");
//        builder.append("  Block Class:   ").append(tryOrNull(() -> block.getClass())).append("\n");
//        builder.append("  Registry Name: ").append(tryOrNull(() -> block.getRegistryName())).append("\n");
//        builder.append("  Metadata:      ").append(tryOrNull(() -> block.getMetaFromState(state))).append("\n");
//        builder.append("  State:         ").append(state).append("\n");
//        builder.append(" Tile at position\n");
//        builder.append("  Tile Class:    ").append(tryOrNull(() -> tile.getClass())).append("\n");
//        builder.append("  Tile Id:       ").append(tryOrNull(() -> TileEntity.getKey(tile.getClass()))).append("\n");
//        builder.append("  Tile NBT:      ").append(tryOrNull(() -> tile.writeToNBT(new NBTTagCompound()))).append("\n");
//        player.sendMessage(new TextComponentString(builder.toString()));
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 0;
//    }
//
//    @Override
//    public String getDesc() {
//        return null;
//    }
//
//    @Override
//    public List<String> getHelp() {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public String getName() {
//        return "dump";
//    }
//}
