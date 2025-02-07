package codechicken.lib.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import org.embeddedt.embeddium.api.BlockRendererRegistry;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Registry for {@link ICCBlockRenderer} instances.
 * <p>
 * Created by covers1624 on 8/09/2016.
 *
 * @see ICCBlockRenderer
 */
@Deprecated(forRemoval = true) // No replacement.
@ScheduledForRemoval (inVersion = "mc 1.21.2+")
public class BlockRenderingRegistry {

    private static boolean initialized = false;

    private static final Map<Block, ICCBlockRenderer> blockRenderers = new HashMap<>();
    private static final Map<Fluid, ICCBlockRenderer> fluidRenderers = new HashMap<>();
    private static final List<ICCBlockRenderer> globalRenderers = new ArrayList<>();

    @OnlyIn (Dist.CLIENT)
    public static void init() {
        if (!initialized) {
            Minecraft mc = Minecraft.getInstance();
            BlockRenderDispatcher parentDispatcher = mc.getBlockRenderer();
            mc.blockRenderer = new CCBlockRendererDispatcher(parentDispatcher, mc.getBlockColors());
            initialized = true;
        }

        if (ModList.get().isLoaded("embeddium")) {
            EmbeddiumSupport.init();
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
    @Deprecated // No replacement. Use a mixin.
    public static synchronized void registerRenderer(Block block, ICCBlockRenderer renderer) {
        ICCBlockRenderer prev = blockRenderers.get(block);
        if (prev != null) {
            throw new IllegalArgumentException("Renderer already registered for block. " + BuiltInRegistries.BLOCK.getKey(block));
        }
        blockRenderers.put(block, renderer);
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
    @Deprecated // No replacement. Use a mixin.
    public static synchronized void registerRenderer(Fluid fluid, ICCBlockRenderer renderer) {
        ICCBlockRenderer prev = fluidRenderers.get(fluid);
        if (prev != null) {
            throw new IllegalArgumentException("Renderer already registered for fluid. " + BuiltInRegistries.FLUID.getKey(fluid));
        }
        fluidRenderers.put(fluid, renderer);
    }

    /**
     * Register a global {@link ICCBlockRenderer}, capable of overriding the
     * rendering of any block in world.
     *
     * @param renderer The {@link ICCBlockRenderer}.
     */
    @Deprecated // No replacement. Use a mixin.
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

        ICCBlockRenderer found = blockRenderers.get(block);
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

        ICCBlockRenderer found = fluidRenderers.get(fluid);
        if (found != null && pred.test(found)) return found;

        return null;
    }

    private static class EmbeddiumSupport {

        private static final Map<ICCBlockRenderer, BlockRendererRegistry.Renderer> ADAPTERS = new ConcurrentHashMap<>();
        private static final ThreadLocal<PoseStack> POSE_STACK_CACHE = ThreadLocal.withInitial(PoseStack::new);

        private static BlockRendererRegistry.Renderer adapt(ICCBlockRenderer renderer) {
            return ADAPTERS.computeIfAbsent(renderer, e -> (ctx, random, consumer) -> {
                e.renderBlock(ctx.state(), ctx.pos(), ctx.world(), POSE_STACK_CACHE.get(), consumer, random, ctx.modelData(), ctx.renderLayer());
                return BlockRendererRegistry.RenderResult.OVERRIDE;
            });
        }

        public static void init() {
            BlockRendererRegistry.instance().registerRenderPopulator((resultList, ctx) -> {
                ICCBlockRenderer renderer = BlockRenderingRegistry.findFor(ctx.state().getBlock(), e -> e.canHandleBlock(ctx.world(), ctx.pos(), ctx.state(), ctx.renderLayer()));
                if (renderer != null) {
                    resultList.add(adapt(renderer));
                }
            });
        }
    }
}
