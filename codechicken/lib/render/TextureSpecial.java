package codechicken.lib.render;

import java.util.ArrayList;
import codechicken.lib.render.SpriteSheetManager.SpriteSheet;
import codechicken.lib.render.TextureUtils.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TextureSpecial extends TextureAtlasSprite implements IIconRegister
{
    //sprite sheet fields
    private int spriteIndex;
    private SpriteSheet spriteSheet;
    
    //textureFX fields
    private TextureFX textureFX;
    
    private int blankSize = -1;

    private ArrayList<TextureDataHolder> baseTextures;
    
    private boolean selfRegister;
    public int atlasIndex;
    
    protected TextureSpecial(String par1)
    {
        super(par1);
    }
    
    public TextureSpecial addTexture(TextureDataHolder t)
    {
        if(baseTextures == null)
            baseTextures = new ArrayList<TextureDataHolder>();
        baseTextures.add(t);
        return this;
    }
    
    public TextureSpecial baseFromSheet(SpriteSheet spriteSheet, int spriteIndex)
    {
        this.spriteSheet = spriteSheet;
        this.spriteIndex = spriteIndex;
        return this;
    }
    
    public TextureSpecial addTextureFX(TextureFX fx)
    {
        textureFX = fx;
        return this;
    }
    
    @Override
    public void func_110971_a(int sheetWidth, int sheetHeight, int originX, int originY, boolean rotated)
    {
        super.func_110971_a(sheetWidth, sheetHeight, originX, originY, rotated);
        if(textureFX != null)
            textureFX.onTextureDimensionsUpdate(field_130223_c, field_130224_d);
    }
    
    @Override
    public void updateAnimation()
    {
        if(textureFX != null)
        {
            textureFX.update();
            if(textureFX.changed())
                TextureUtil.func_110998_a(textureFX.imageData, field_130223_c, field_130224_d, field_110975_c, field_110974_d, false, false);
        }
    }
    
    @Override
    public boolean load(ResourceManager manager, ResourceLocation location)
    {
        if(baseTextures != null)
        {
            for(TextureDataHolder tex : baseTextures)
            {
                field_110976_a.add(tex.data);
                field_130223_c = tex.width;
                field_130224_d = tex.height;
            }
        }
        
        if(spriteSheet != null)
        {
            TextureDataHolder tex = spriteSheet.createSprite(spriteIndex);
            field_130223_c = tex.width;
            field_130224_d = tex.height;
            field_110976_a.add(tex.data);
        }
        
        if(blankSize > 0)
        {
            field_130223_c = field_130224_d = blankSize;
            field_110976_a.add(new int[blankSize*blankSize]);
        }
        
        if(field_110976_a.isEmpty())
            throw new RuntimeException("No base frame for texture: "+getIconName());
        
        return true;
    }
    
    @Override
    public boolean func_130098_m()
    {
        return textureFX != null || super.func_130098_m();
    }
    
    @Override
    public int func_110970_k()
    {
        if(textureFX != null)
            return 1;
        
        return super.func_110970_k();
    }

    public TextureSpecial blank(int size)
    {
        blankSize = size;
        return this;
    }
    
    public TextureSpecial selfRegister()
    {
        selfRegister = true;
        TextureUtils.addIconRegistrar(this);
        return this;
    }
    
    @Override
    public void registerIcons(IconRegister register)
    {
        if(selfRegister)
            ((TextureMap)register).setTextureEntry(getIconName(), this);
    }
    
    @Override
    public int atlasIndex()
    {
        return atlasIndex;
    }
}
