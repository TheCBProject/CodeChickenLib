package codechicken.lib.block.component.data;

import net.minecraft.data.DataProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data generator component for defining Block tags.
 * <p>
 * Created by covers1624 on 22/7/22.
 *
 * @see DataGenComponent
 */
@ApiStatus.Experimental
public class TagComponent extends DataGenComponent {

    public final TagKey<Block> tag;

    public TagComponent(TagKey<Block> tag) {
        this.tag = tag;
    }

    @Override
    protected void addToProvider(DataProvider provider) {
        if (provider instanceof BlockTagsProvider p) {
            p.tag(tag).add(getBlock());
        }
    }
}
