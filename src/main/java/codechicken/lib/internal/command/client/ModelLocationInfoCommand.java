package codechicken.lib.internal.command.client;

import codechicken.lib.command.ClientCommandBase;
import codechicken.lib.command.help.IBetterHelpCommand;
import codechicken.lib.internal.CCLLog;
import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.command.CommandException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by covers1624 on 9/06/18.
 */
@SideOnly (Side.CLIENT)
public class ModelLocationInfoCommand extends ClientCommandBase implements IBetterHelpCommand {

    private static Map<IRegistryDelegate<Item>, TIntObjectHashMap<ModelResourceLocation>> immf_locationsCache;
    private static Map<IRegistryDelegate<Item>, TIntObjectHashMap<IBakedModel>> immf_modelsCache;
    private static Map<Item, ItemMeshDefinition> imm_shapersCache;
    private static ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");

    @SuppressWarnings ("unchecked")
    private static void pullCache(Minecraft mc) throws CommandException {
        try {
            if (immf_locationsCache == null || immf_modelsCache == null || imm_shapersCache == null) {
                RenderItem renderItem = mc.getRenderItem();
                ItemModelMesher mesher = renderItem.getItemModelMesher();
                String cls = ItemModelMesherForge.class.getName().replace(".", "/");
                String cls2 = ItemModelMesher.class.getName().replace(".", "/");
                ObfMapping locationsMapping = new ObfMapping(cls, "locations", "Ljava/util/Map;");
                ObfMapping modelsMapping = new ObfMapping(cls, "locations", "Ljava/util/Map;");
                ObfMapping shapersField = new ObfMapping(cls2, "field_178092_c", "Ljava/util/Map;");
                immf_locationsCache = ReflectionManager.getField(locationsMapping, mesher, Map.class);
                immf_modelsCache = ReflectionManager.getField(modelsMapping, mesher, Map.class);
                imm_shapersCache = ReflectionManager.getField(shapersField, mesher, Map.class);
            }
        } catch (Exception e) {
            CCLLog.log(Level.ERROR, e, "Unable to pull cache.");
            throw new CommandException("Unable to update cache, see log.");
        }
    }

    @Override
    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
        pullCache(mc);
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) {
            stack = player.getHeldItemOffhand();
            if (stack.isEmpty()) {
                player.sendMessage(new TextComponentString("You do not appear to be holding anything."));
                return;
            }
        }
        Item item = stack.getItem();
        String itemLoc;
        {
            ModelResourceLocation loc = null;
            if (immf_modelsCache.containsKey(item.delegate)) {
                loc = immf_locationsCache.get(item.delegate).get(stack.getMaxDamage() > 0 ? 0 : stack.getMetadata());
            } else {
                ItemMeshDefinition mesher = imm_shapersCache.get(item);
                if (mesher != null) {
                    loc = mesher.getModelLocation(stack);
                }
            }
            if (loc == null) {
                loc = MODEL_MISSING;
            }
            itemLoc = loc.toString();
        }
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
