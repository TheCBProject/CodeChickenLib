package codechicken.lib.block;

import codechicken.lib.block.ModularTileBlock.TileComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 19/7/22.
 */
@ApiStatus.Experimental
public abstract class ModularBlockEntity extends BlockEntity {

    private final ModularTileBlock<?> block;
    private final DataComponent[] components;

    public ModularBlockEntity(BlockEntityType<?> tileType, BlockPos pos, BlockState state) {
        super(tileType, pos, state);
        Block bl = state.getBlock();
        if (!(bl instanceof ModularTileBlock)) {
            throw new IllegalStateException("ModularBlockEntity constructed with the incorrect Block! Expected a ModularTileBlock. Got: " + bl.getClass().getName() + " State: " + state);
        }
        block = (ModularTileBlock<?>) bl;

        components = new DataComponent[block.namedComponents.size()];
        for (TileComponent<?> component : block.namedComponents.values()) {
            components[component.id] = component.createData(this);
        }
    }

    public final <T extends DataComponent> T getData(TileComponent<T> component) {
        assert block.namedComponents.get(component.name) == component;

        return unsafeCast(components[component.id]);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        for (DataComponent component : components) {
            CompoundTag componentTag = new CompoundTag();
            component.save(componentTag);
            tag.put(component.tileComponent.name, componentTag);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void load(CompoundTag tag) {
        super.load(tag);

        for (DataComponent component : components) {
            if (tag.contains(component.tileComponent.name)) {
                component.load(tag.getCompound(component.tileComponent.name));
            }
        }
    }

    public static abstract class DataComponent {

        protected final ModularBlockEntity tile;
        protected final TileComponent<?> tileComponent;

        protected DataComponent(ModularBlockEntity tile, TileComponent<?> tileComponent) {
            this.tile = tile;
            this.tileComponent = tileComponent;
        }

        protected void save(CompoundTag tag) {
        }

        protected void load(CompoundTag tag) {
        }
    }
}
