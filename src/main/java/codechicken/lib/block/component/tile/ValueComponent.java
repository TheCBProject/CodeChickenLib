package codechicken.lib.block.component.tile;

import codechicken.lib.block.ModularBlockEntity;
import codechicken.lib.block.ModularTileBlock;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by covers1624 on 3/29/26.
 */
@ApiStatus.Experimental
public class ValueComponent<V> extends ModularTileBlock.TileComponent<ValueComponent<V>.Data> {

    private final Codec<V> codec;
    private final V defaultValue;

    private @Nullable StreamCodec<RegistryFriendlyByteBuf, V> streamCodec;

    public ValueComponent(Codec<V> codec, V defaultValue) {
        this.codec = codec;
        this.defaultValue = defaultValue;
    }

    public ValueComponent<V> syncToClient(StreamCodec<RegistryFriendlyByteBuf, V> codec) {
        streamCodec = codec;
        return this;
    }

    @Override
    protected Data createData(ModularBlockEntity ent) {
        return new Data(ent, this);
    }

    public class Data extends ModularBlockEntity.DataComponent {

        private final List<Consumer<V>> onChanged = new ArrayList<>(0);

        private V value = defaultValue;

        protected Data(ModularBlockEntity tile, ModularTileBlock.TileComponent<?> tileComponent) {
            super(tile, tileComponent);
        }

        public Data onChanged(Consumer<V> cons) {
            onChanged.add(cons);
            return this;
        }

        public V get() {
            return value;
        }

        public void set(V value) {
            if (!this.value.equals(value)) {
                forceSet(value);
                onChanged();
            }
        }

        public void forceSet(V value) {
            this.value = value;
        }

        public void onChanged() {
            onChanged.forEach(e -> e.accept(value));

            syncToClient();
        }

        public void syncToClient() {
            if (tile.getLevel().isClientSide()) return;
            if (streamCodec == null) throw new UnsupportedOperationException("No stream codec supplied.");

            sendToClient(null, p -> {
                p.cc$writeWithRegistryCodec(streamCodec, value);
            });
        }

        @Override
        protected void onClientPacket(RegistryFriendlyByteBuf buf, IPayloadContext ctx) {
            if (streamCodec == null) throw new IllegalStateException("Somehow received packet for component which does not have a codec..");

            forceSet(buf.cc$readWithRegistryCodec(streamCodec));
            onChanged();
        }

        @Override
        protected void save(ValueOutput output) {
            super.save(output);
            output.store("value", codec, value);
        }

        @Override
        protected void load(ValueInput input) {
            super.load(input);
            value = input.read("value", codec).orElse(defaultValue);
        }
    }
}
