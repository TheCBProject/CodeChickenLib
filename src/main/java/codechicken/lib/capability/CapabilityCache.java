package codechicken.lib.capability;

import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple cache for capabilities as viewed from a specific world position.
 * <p>
 * Provides a cleaner api to the Neo provided {@link BlockCapabilityCache}. The Neo cache requires you know, up-front,
 * what capabilities you wish to receive, this is not always the case and not always easily computable.
 * <p>
 * For Support of API's that notify you when your host was moved, you should call {@link #setLevelPos}
 * to ensure the capability cache
 * <p>
 * It is also possible to create {@link CapabilityCache} without a world and position. Doing so, will
 * cause all getters to return 'empty' until one is assigned.
 * <p>
 * Created by covers1624 on 4/19/20.
 */
public class CapabilityCache {

    private final SideCache[] cache;

    private @Nullable ServerLevel level;
    private @Nullable BlockPos pos;
    private int moveCookie;

    public CapabilityCache() {
        cache = new SideCache[7];
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new SideCache();
        }
    }

    public CapabilityCache(ServerLevel level, BlockPos pos) {
        this();
        setLevelPos(level, pos);
    }

    /**
     * Call this when {@link CapabilityCache}'s host has been moved.<br/>
     * Clears all internal state and sets a new world / pos.
     *
     * @param level The new world.
     * @param pos   The new pos.
     */
    public void setLevelPos(ServerLevel level, BlockPos pos) {
        clear();
        this.level = level;
        this.pos = pos;
        this.moveCookie++;
    }

    /**
     * Clears all internal state. Everything will be re-cached.
     */
    public void clear() {
        for (SideCache sideCache : cache) {
            sideCache.cache.clear();
            sideCache.listener = null;
        }
    }

    /**
     * Overload of {@link #getCapability} with support for returning another object when empty.
     *
     * @param capability The capability to get.
     * @param to         The direction to ask.
     * @param default_   The object to return when empty.
     * @return The instance, either the capability at <code>to</code> or <code>default_</code>
     */
    public <T> T getCapabilityOr(BlockCapability<T, Direction> capability, Direction to, T default_) {
        T ret = getCapability(capability, to);
        if (ret == null) return default_;

        return ret;
    }

    /**
     * Gets a capability from the block in <code>to</code> direction from {@link CapabilityCache}'s
     * position. For example, calling this with <code>NORTH</code>, will get a capability from the block
     * IN <code>NORTH</code> direction on ITS <code>SOUTH</code> face.
     *
     * @param capability The capability to get.
     * @param to         The direction to ask.
     * @return The capability instance or {@code null}.
     */
    public <T> @Nullable T getCapability(BlockCapability<T, @Nullable Direction> capability, Direction to) {
        return getCapability(capability, to, to.getOpposite());
    }

    /**
     * Gets a capability from the block in <code>to</code> direction from {@link CapabilityCache}'s
     * position.
     *
     * @param capability The capability to get.
     * @param to         The direction to ask.
     * @param ctx        The context, usually the face.
     * @return The capability instance or {@code null}.
     */
    public <T, C> @Nullable T getCapability(BlockCapability<T, C> capability, Direction to, C ctx) {
        Objects.requireNonNull(capability, "Null capability.");
        if (level == null || pos == null) return null;

        SideCache sideCache = cache[idxForDir(to)];
        CacheKey key = new CacheKey(capability, ctx);
        CacheValue<T> val = SneakyUtils.unsafeCast(sideCache.cache.get(key));
        if (val != null) return val.obj;

        BlockPos there = pos.relative(to);
        T obj;
        if (level.isLoaded(there)) {
            obj = level.getCapability(capability, there, ctx);
            sideCache.addListener(there);
        } else {
            obj = null;
        }
        sideCache.cache.put(key, new CacheValue<>(obj));
        return obj;
    }

    private static int idxForDir(@Nullable Direction dir) {
        return dir == null ? 6 : dir.ordinal();
    }

    private final class SideCache {

        private final Map<CacheKey, CacheValue<?>> cache = new HashMap<>();
        private @Nullable ICapabilityInvalidationListener listener;

        public void addListener(BlockPos pos) {
            if (listener != null) return;
            if (level == null) throw new RuntimeException("Level is null?? What?");

            listener = new ICapabilityInvalidationListener() {
                private final int cookie = moveCookie;

                @Override
                public boolean onInvalidate() {
                    if (cookie != moveCookie) return false;

                    cache.clear();
                    return true;
                }
            };
            level.registerCapabilityListener(pos, listener);
        }
    }

    private record CacheKey(BlockCapability<?, ?> capability, @Nullable Object context) { }

    private record CacheValue<T>(@Nullable T obj) { }

}
