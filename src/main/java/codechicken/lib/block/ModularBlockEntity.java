package codechicken.lib.block;

import codechicken.lib.block.ModularTileBlock.TileComponent;
import codechicken.lib.internal.CCLNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 19/7/22.
 */
@ApiStatus.Experimental
public abstract class ModularBlockEntity extends BlockEntity {

    private static final byte COMPONENT_MESSAGE = 1;

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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        for (DataComponent component : components) {
            component.save(output.child(component.tileComponent.name));
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        for (DataComponent component : components) {
            var childIn = input.child(component.tileComponent.name);
            childIn.ifPresent(component::load);
        }
    }

    private void sendServerPacket(Consumer<RegistryFriendlyByteBuf> cons) {
        var packet = CCLNetwork.TILE_MESSAGE.toServer();
        packet.writeBlockPos(getBlockPos());
        cons.accept(packet);
        packet.sendToServer();
    }

    public final void onInternalServerPacket(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        switch (packet.readUnsignedByte()) {
            case COMPONENT_MESSAGE -> components[packet.readVarInt()].onServerPacket(packet, ctx);
        }
    }

    private void sendClientPacket(@Nullable ServerPlayer to, Consumer<RegistryFriendlyByteBuf> cons) {
        var packet = CCLNetwork.TILE_MESSAGE.toClient(this);
        packet.writeBlockPos(getBlockPos());
        cons.accept(packet);
        if (to == null) {
            packet.sendToChunk(this);
        } else {
            packet.sendToPlayer(to);
        }
    }

    public final void onInternalClientPacket(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        switch (packet.readUnsignedByte()) {
            case COMPONENT_MESSAGE -> components[packet.readVarInt()].onClientPacket(packet, ctx);
        }
    }

    public static abstract class DataComponent {

        protected final ModularBlockEntity tile;
        protected final TileComponent<?> tileComponent;

        protected DataComponent(ModularBlockEntity tile, TileComponent<?> tileComponent) {
            this.tile = tile;
            this.tileComponent = tileComponent;
        }

        protected final void sendToServer(Consumer<RegistryFriendlyByteBuf> cons) {
            tile.sendServerPacket(p -> {
                p.writeByte(COMPONENT_MESSAGE);
                p.writeVarInt(tileComponent.id);
                cons.accept(p);
            });
        }

        protected void onServerPacket(RegistryFriendlyByteBuf buf, IPayloadContext ctx) {
        }

        protected final void sendToClient(@Nullable ServerPlayer to, Consumer<RegistryFriendlyByteBuf> cons) {
            tile.sendClientPacket(to, p -> {
                p.writeByte(COMPONENT_MESSAGE);
                p.writeVarInt(tileComponent.id);
                cons.accept(p);
            });
        }

        protected void onClientPacket(RegistryFriendlyByteBuf buf, IPayloadContext ctx) {
        }

        protected void save(ValueOutput output) {
        }

        protected void load(ValueInput input) {
        }
    }
}
