//package codechicken.lib.model.bakery.sub;
//
//import codechicken.lib.model.bakery.ModelBakery;
//import codechicken.lib.model.bakery.key.IBlockStateKeyGenerator;
//import net.minecraftforge.common.property.IExtendedBlockState;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by covers1624 on 27/12/2016.
// * TODO Document.
// */
//public class SubBlockStateKeyGenerator implements IBlockStateKeyGenerator {
//
//    private final Map<Integer, IBlockStateKeyGenerator> subKeyGenMap = new HashMap<>();
//
//    public void register(int meta, IBlockStateKeyGenerator subGenerator) {
//        subKeyGenMap.put(meta, subGenerator);
//    }
//
//    @Override
//    public String generateKey(IExtendedBlockState state) {
//        int meta = state.getBlock().getMetaFromState(state);
//        if (subKeyGenMap.containsKey(meta)) {
//            IBlockStateKeyGenerator keyGenerator = subKeyGenMap.get(meta);
//            return keyGenerator.generateKey(state);
//        }
//        return ModelBakery.defaultBlockKeyGenerator.generateKey(state);
//    }
//}
