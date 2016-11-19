package codechicken.lib.asm;

import codechicken.lib.asm.ModularASMTransformer.MethodInjector;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Map;

/**
 * Created by covers1624 on 17/11/2016.
 */
public class BlockStateLoaderTransformer implements IClassTransformer {

    private ModularASMTransformer transformer = new ModularASMTransformer();

    public BlockStateLoaderTransformer() {
        Map<String, ASMBlock> blocks = ASMReader.loadResource("/assets/ccl/asm/hooks.asm");
        ObfMapping mapping = new ObfMapping("net/minecraft/client/renderer/block/model/ModelBlockDefinition", "func_178331_a", "(Ljava/io/Reader;)Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;");
        transformer.add(new MethodInjector(mapping, blocks.get("i_BlockStateLoader"), true));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return transformer.transform(name, basicClass);
    }
}
