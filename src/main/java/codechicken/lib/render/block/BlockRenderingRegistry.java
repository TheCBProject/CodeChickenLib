package codechicken.lib.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

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

    private static final Map<Holder<Block>, ICCBlockRenderer> blockRenderers = new HashMap<>();
    private static final Map<Holder<Fluid>, ICCBlockRenderer> fluidRenderers = new HashMap<>();
    private static final List<ICCBlockRenderer> globalRenderers = new ArrayList<>();

    @OnlyIn (Dist.CLIENT)
    public static void init() {
        if (!initialized) {
            Minecraft mc = Minecraft.getInstance();
            BlockRenderDispatcher parentDispatcher = mc.getBlockRenderer();
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
        Holder<Block> delegate = ForgeRegistries.BLOCKS.getDelegateOrThrow(block);
        ICCBlockRenderer prev = blockRenderers.get(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
        if (prev != null) {
            throw new IllegalArgumentException("Renderer already registered for block. " + ForgeRegistries.BLOCKS.getKey(block));
        }
        blockRenderers.put(delegate, renderer);
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
        Holder<Fluid> delegate = ForgeRegistries.FLUIDS.getDelegateOrThrow(fluid);
        ICCBlockRenderer prev = blockRenderers.get(delegate);
        if (prev != null) {
            throw new IllegalArgumentException("Renderer already registered for fluid. " + ForgeRegistries.FLUIDS.getKey(fluid));
        }
        fluidRenderers.put(delegate, renderer);
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

    @Nullable
    static ICCBlockRenderer findFor(Block block, Predicate<ICCBlockRenderer> pred) {
        for (ICCBlockRenderer renderer : globalRenderers) {
            if (pred.test(renderer)) {
                return renderer;
            }
        }

        ICCBlockRenderer found = blockRenderers.get(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
        if (found != null && pred.test(found)) return found;

        return null;
    }

    @Nullable
    static ICCBlockRenderer findFor(Fluid fluid, Predicate<ICCBlockRenderer> pred) {
        for (ICCBlockRenderer renderer : globalRenderers) {
            if (pred.test(renderer)) {
                return renderer;
            }
        }

        ICCBlockRenderer found = fluidRenderers.get(ForgeRegistries.FLUIDS.getDelegateOrThrow(fluid));
        if (found != null && pred.test(found)) return found;

        return null;
    }
}
