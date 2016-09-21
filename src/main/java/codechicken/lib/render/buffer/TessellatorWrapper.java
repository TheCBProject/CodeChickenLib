package codechicken.lib.render.buffer;

import net.minecraft.client.renderer.Tessellator;

/**
 * Created by covers1624 on 20/09/2016.
 */
public class TessellatorWrapper extends VertexBufferWrapper {
    private Tessellator tessellator;

    public TessellatorWrapper(Tessellator tessellator){
        super(tessellator.getBuffer());
        this.tessellator = tessellator;
    }

    @Override
    public void draw() {
        tessellator.draw();
    }
}
