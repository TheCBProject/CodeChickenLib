package codechicken.lib.gui.modular.lib;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;

/**
 * Created by brandon3055 on 29/06/2023
 */
public class ScissorHandler {
    public static final Logger LOGGER = LogManager.getLogger();
    private final Deque<ScissorState> stack = Queues.newArrayDeque();

    public void pushGuiScissor(double x, double y, double width, double height) {
        Window window = Minecraft.getInstance().getWindow();
        int windowHeight = window.getHeight();
        double scale = window.getGuiScale();
        double scX = x * scale;
        double scY = (double) windowHeight - (y + height) * scale;
        double scW = Math.max(width * scale, 0);
        double scH = Math.max(height * scale, 0);
        pushScissor((int) scX, (int) scY, (int) scW, (int) scH);
    }

    public void pushScissor(int x, int y, int width, int height) {
        int xMax = x + width;
        int yMax = y + height;
        stack.addLast(new ScissorState(x, y, xMax, yMax, stack.peekLast()).apply());
    }

    public void popScissor() {
        if (stack.isEmpty()) {
            LOGGER.error("Scissor stack underflow");
        }
        stack.removeLast();
        ScissorState active = stack.peekLast();
        if (active != null) {
            active.apply();
        } else {
            RenderSystem.disableScissor();
        }
    }

    private static class ScissorState {
        private int x;
        private int y;
        private int xMax;
        private int yMax;

        private ScissorState(int x, int y, int xMax, int yMax, ScissorState prevState) {
            if (prevState != null) {
                this.x = Math.max(prevState.x, x);
                this.y = Math.max(prevState.y, y);
                this.xMax = Math.min(prevState.xMax, xMax);
                this.yMax = Math.min(prevState.yMax, yMax);
                Minecraft mc = Minecraft.getInstance();
                if (this.x < 0) this.x = 0;
                if (this.y < 0) this.y = 0;
                if (this.xMax > mc.getWindow().getScreenWidth()) this.xMax = mc.getWindow().getScreenWidth();
                if (this.yMax > mc.getWindow().getScreenHeight()) this.yMax = mc.getWindow().getScreenHeight();
                if (this.xMax < this.x) this.xMax = this.x;
                if (this.yMax < this.y) this.yMax = this.y;
            } else {
                this.x = x;
                this.y = y;
                this.xMax = xMax;
                this.yMax = yMax;
            }
        }

        private ScissorState apply() {
            RenderSystem.enableScissor(x, y, xMax - x, yMax - y);
            return this;
        }
    }
}
