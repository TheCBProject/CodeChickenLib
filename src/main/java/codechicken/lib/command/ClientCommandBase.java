//package codechicken.lib.command;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.server.MinecraftServer;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
///**
// * Just a helper for a cleaner execute method.
// * Created by covers1624 on 9/06/18.
// */
//@SideOnly (Side.CLIENT)
//public abstract class ClientCommandBase extends CommandBase {
//
//    @Override
//    public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (!(sender instanceof EntityPlayerSP)) {
//            throw new CommandException("It appears you arent an instance of EntityPlayerSP. Unable to process.");
//        }
//        execute(Minecraft.getMinecraft(), (EntityPlayerSP) sender, args);
//    }
//
//    public abstract void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException;
//}
