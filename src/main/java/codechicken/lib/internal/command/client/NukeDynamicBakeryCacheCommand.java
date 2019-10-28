//package codechicken.lib.internal.command.client;
//
//import codechicken.lib.command.ClientCommandBase;
//import codechicken.lib.command.help.IBetterHelpCommand;
//import codechicken.lib.model.bakery.ModelBakery;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.command.CommandException;
//import net.minecraft.util.text.TextComponentString;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by covers1624 on 9/06/18.
// */
//public class NukeDynamicBakeryCacheCommand extends ClientCommandBase implements IBetterHelpCommand {
//
//    @Override
//    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
//        ModelBakery.nukeModelCache();
//        player.sendMessage(new TextComponentString("Model cache nuked!"));
//    }
//
//    @Override
//    public String getName() {
//        return "nuke_dyn_bakery_cache";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 0;
//    }
//
//    @Override
//    public String getDesc() {
//        return "Clears CCL's Dynamic bakery cache.";
//    }
//
//    @Override
//    public List<String> getHelp() {
//        List<String> lines = new ArrayList<>();
//        lines.add("This clears the model cache for CCL's Dynamic bakery.");
//        lines.add("This may cause a dip in performance as models are re-baked.");
//        lines.add("Really only a dev tool, but its here for others if they need.");
//        return lines;
//    }
//}
