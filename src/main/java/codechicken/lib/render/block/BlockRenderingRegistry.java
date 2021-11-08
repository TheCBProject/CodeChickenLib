package codechicken.lib.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Registry for {@link ICCBlockRenderer} instances.
 * <p>
 * Created by covers1624 on 8/09/2016.
 *
 * @see ICCBlockRenderer
 */
public class BlockRenderingRegistry {

    private static boolean initialized = false;

    private static final Map<IRegistryDelegate<Block>, ICCBlockRenderer> blockRenderers = new HashMap<>();
    private static final Map<IRegistryDelegate<Fluid>, ICCBlockRenderer> fluidRenderers = new HashMap<>();
    private static final List<ICCBlockRenderer> globalRenderers = new ArrayList<>();

    @OnlyIn (Dist.CLIENT)
    public static void init() {
        if (!initialized) {
            Minecraft mc = Minecraft.getInstance();
            BlockRendererDispatcher parentDispatcher = mc.getBlockRenderer();
            mc.blockRenderer = new CCBlockRendererDispatcher(parentDispatcher, mc.getBlockColors());
            initialized = true;
        }
    }

    /**
     * Register an {@link ICCBlockRenderer} for the given block.
     * <p>
     * Unlike {@link #registerGlobalRenderer}, registering your {@link ICCBlockRenderer} via this method
     * is guaranteed to only be tested against the block provided.
     *
     * @param block    The block to register to.
     * @param renderer The {@link ICCBlockRenderer}.
     * @throws IllegalArgumentException If the same Block is registered twice.
     */
    public static synchronized void registerRenderer(Block block, ICCBlockRenderer renderer) {
        ICCBlockRenderer prev = blockRenderers.get(block.delegate);
        if (prev != null) {
            throw new IllegalArgumentException("Renderer already registered for block. " + block.getRegistryName());
        }
        blockRenderers.put(block.delegate, renderer);
    }

    /**
     * Register an {@link ICCBlockRenderer} for the given fluid.
     * <p>
     * Unlike {@link #registerGlobalRenderer}, registering your {@link ICCBlockRenderer} via this method
     * is guaranteed to only be tested against the fluid provided.
     *
     * @param fluid    The fluid to register to.
     * @param renderer The {@link ICCBlockRenderer}.
     * @throws IllegalArgumentException If the same Fluid is registered twice.
     */
    public static synchronized void registerRenderer(Fluid fluid, ICCBlockRenderer renderer) {
        ICCBlockRenderer prev = blockRenderers.get(fluid.delegate);
        if (prev != null) {
            throw new IllegalArgumentException("Renderer already registered for fluid. " + fluid.getRegistryName());
        }
        fluidRenderers.put(fluid.delegate, renderer);
    }

    /**
     * Register a global {@link ICCBlockRenderer}, capable of overriding the
     * rendering of any block in world.
     *
     * @param renderer The {@link ICCBlockRenderer}.
     */
    public static synchronized void registerGlobalRenderer(ICCBlockRenderer renderer) {
        globalRenderers.add(renderer);
    }

    /**
     * Renamed to {@link #registerGlobalRenderer}
     */
    @Deprecated // TODO 1.17
    public static synchronized void registerRenderer(ICCBlockRenderer renderer) {
        registerGlobalRenderer(renderer);
    }

    @Nullable
    static ICCBlockRenderer findFor(Block block, Predicate<ICCBlockRenderer> pred) {
        ICCBlockRenderer found = blockRenderers.get(block.delegate);
        if (found != null && pred.test(found)) return found;

        for (ICCBlockRenderer renderer : globalRenderers) {
            if (pred.test(renderer)) {
                return renderer;
            }
        }
        return null;
    }

    @Nullable
    static ICCBlockRenderer findFor(Fluid fluid, Predicate<ICCBlockRenderer> pred) {
        ICCBlockRenderer found = fluidRenderers.get(fluid.delegate);
        if (found != null && pred.test(found)) return found;

        for (ICCBlockRenderer renderer : globalRenderers) {
            if (pred.test(renderer)) {
                return renderer;
            }
        }
        return null;
    }
}
