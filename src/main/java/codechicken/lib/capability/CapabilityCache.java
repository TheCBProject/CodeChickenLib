package codechicken.lib.capability;

import codechicken.lib.math.MathHelper;
import codechicken.lib.util.Object2IntPair;
import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.*;

/**
 * A simple cache for capabilities as viewed from a specific world position.
 * <p>
 * Several things need to be implemented depending on your usage, if you wish to have a time-out
 * based cache, meaning if a capability for a block is 'empty', it will wait {@link #setWaitTicks}
 * number of ticks to check again, if you are using this class in that mode, you must call
 * {@link #tick()} each game tick otherwise nothing will ever re-cache. <br/>
 * You can also call {@link #onNeighborChanged}, doing so will force a re-cache of all empty capabilities
 * from the block that notified us of a change, on the next query.<br/>
 * For Support of API's that notify you when your host was moved, you should probably call {@link #setWorldPos}
 * to clear the entire cache and notify of world and/or position change.
 * <p>
 * It is also possible to create {@link CapabilityCache} without a world and position. Doing so, will
 * cause all getters to return 'empty' until one is assigned.
 * <p>
 * Created by covers1624 on 4/19/20.
 */
public class CapabilityCache {

    private final Map<Capability<?>, Object2IntPair<LazyOptional<?>>> selfCache = new HashMap<>();
    private final EnumMap<Direction, Map<Capability<?>, Object2IntPair<LazyOptional<?>>>> sideCache = new EnumMap<>(Direction.class);

    private Level world;
    private BlockPos pos;

    private int ticks;
    private int waitTicks = 5 * 20;

    public CapabilityCache() {
    }

    public CapabilityCache(Level world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    /**
     * Sets the number of ticks to wait between re-caching an 'empty' capability.<br/>
     * The default is to wait 5 seconds.
     *
     * @param ticks The number of game ticks to wait.
     */
    public void setWaitTicks(int ticks) {
        this.waitTicks = ticks;
    }

    /**
     * Call this to notify of a new game tick.
     */
    public void tick() {
        ticks++;
    }

    /**
     * Call this when {@link CapabilityCache}'s host has been moved.<br/>
     * Clears all internal state and sets a new world / pos.
     *
     * @param world The new world.
     * @param pos   The new pos.
     */
    public void setWorldPos(Level world, BlockPos pos) {
        clear();
        this.world = world;
        this.pos = pos;
    }

    /**
     * Clears all internal state. Everything will be re-cached.
     */
    public void clear() {
        selfCache.clear();
        Arrays.stream(Direction.BY_3D_DATA).map(this::getCacheForSide).forEach(Map::clear);
    }

    /**
     * Notifies {@link CapabilityCache} of a {@link Block#onNeighborChange} event.<br/>
     * Marks all empty capabilities provided by <code>from</code> block, to be re-cached
     * next query.
     *
     * @param from The from position.
     */
    public void onNeighborChanged(BlockPos from) {
        if (world == null || pos == null) {
            return;
        }
        BlockPos offset = from.subtract(pos);
        int diff = MathHelper.absSum(offset);
        int side = MathHelper.toSide(offset);
        if (side < 0 || diff != 1) {
            return;
        }
        Direction sideChanged = Direction.BY_3D_DATA[side];

        Iterables.concat(selfCache.entrySet(), getCacheForSide(sideChanged).entrySet()).forEach(entry -> {
            Object2IntPair<LazyOptional<?>> pair = entry.getValue();
            if (pair.getKey() != null && !pair.getKey().isPresent()) {
                pair.setKey(null);
                pair.setValue(ticks);
            }
        });
    }

    /**
     * Overload of {@link #getCapability} with support for evaluating a {@link NonNullSupplier}
     * when empty.
     *
     * @param capability The capability to get.
     * @param to         The direction to ask.
     * @param default_   The supplier to evaluate when empty.
     * @return The instance, either the capability at <code>to</code> or what <code>default_</code> supplies.
     */
    public <T> T getCapabilityOr(Capability<T> capability, Direction to, NonNullSupplier<T> default_) {
        return getCapability(capability, to).orElseGet(default_);
    }

    /**
     * Overload of {@link #getCapability} with support for returning another object when empty.
     *
     * @param capability The capability to get.
     * @param to         The direction to ask.
     * @param default_   The object to return when empty.
     * @return The instance, either the capability at <code>to</code> or <code>default_</code>
     */
    public <T> T getCapabilityOr(Capability<T> capability, Direction to, T default_) {
        return getCapability(capability, to).orElse(default_);
    }

    /**
     * Gets a capability from the block in <code>to</code> direction from {@link CapabilityCache}'s
     * position. For example, calling this with <code>NORTH</code>, will get a capability from the block
     * IN <code>NORTH</code> direction on ITS <code>SOUTH</code> face.
     *
     * @param capability The capability to get.
     * @param to         The direction to ask.
     * @return A {@link LazyOptional} of the capability, may be empty.
     */
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction to) {
        Objects.requireNonNull(capability, "Null capability.");
        if (world == null || pos == null) {
            return LazyOptional.empty().cast();
        }
        Map<Capability<?>, Object2IntPair<LazyOptional<?>>> sideCache = getCacheForSide(to);
        Object2IntPair<LazyOptional<?>> cache = sideCache.get(capability);
        if (cache == null) {
            cache = new Object2IntPair<>(null, ticks);
            sideCache.put(capability, cache);
            return tryReCache(capability, to, cache).cast();
        }
        LazyOptional<?> lookup = cache.getKey();
        if (lookup == null || !lookup.isPresent()) {
            return tryReCache(capability, to, cache).cast();
        }
        return lookup.cast();
    }

    //TODO this logic should be unified and merged with above, There is a potential case where we could leak the Object2IntPair
    //TODO  instance if a mod does weird things with their invalidations of LazyOptionals
    private LazyOptional<?> tryReCache(Capability<?> capability, Direction to, Object2IntPair<LazyOptional<?>> cache) {
        boolean isFirst = cache.getKey() == null;
        if (isFirst || !cache.getKey().isPresent()) {
            if (isFirst || cache.getValue() + waitTicks <= ticks) {
                LazyOptional<?> lookup = requestCapability(capability, to);
                if (lookup.isPresent()) {
                    cache.setKey(lookup);
                    cache.setValue(ticks);
                    lookup.addListener(l -> {//TODO, probably not needed? we check every lookup anyway..
                        //When the LazyOptional notifies us that its gone,
                        //set the cache to empty, and mark ticks.
                        cache.setKey(LazyOptional.empty());
                        cache.setValue(ticks);
                    });
                } else {
                    cache.setKey(LazyOptional.empty());
                    cache.setValue(ticks);
                }
            }
        }
        return cache.getKey();
    }

    private LazyOptional<?> requestCapability(Capability<?> capability, Direction to) {
        BlockEntity tile = world.getBlockEntity(pos.relative(to));
        Direction inverse = to == null ? null : to.getOpposite();
        if (tile != null) {
            return tile.getCapability(capability, inverse);
        }
        return LazyOptional.empty();
    }

    private Map<Capability<?>, Object2IntPair<LazyOptional<?>>> getCacheForSide(Direction side) {
        if (side == null) {
            return selfCache;
        }
        return sideCache.computeIfAbsent(side, s -> new HashMap<>());
    }

}
