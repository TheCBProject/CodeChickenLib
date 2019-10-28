//package codechicken.lib.model.bakery.sub;
//
//import codechicken.lib.model.bakery.ModelBakery;
//import codechicken.lib.model.bakery.key.IItemStackKeyGenerator;
//import net.minecraft.item.ItemStack;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by covers1624 on 30/12/2016.
// * TODO Document.
// */
//public class SubItemStackKeyGenerator implements IItemStackKeyGenerator {
//
//    private final Map<Integer, IItemStackKeyGenerator> subKeyGenMap = new HashMap<>();
//
//    public void register(int meta, IItemStackKeyGenerator subGenerator) {
//        subKeyGenMap.put(meta, subGenerator);
//    }
//
//    @Override
//    public String generateKey(ItemStack stack) {
//        int meta = stack.getMetadata();
//        if (subKeyGenMap.containsKey(meta)) {
//            IItemStackKeyGenerator generator = subKeyGenMap.get(meta);
//            return generator.generateKey(stack);
//        }
//        return ModelBakery.defaultItemKeyGenerator.generateKey(stack);
//    }
//}
