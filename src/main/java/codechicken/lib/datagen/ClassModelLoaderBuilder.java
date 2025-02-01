package codechicken.lib.datagen;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static codechicken.lib.CodeChickenLib.MOD_ID;
import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 1/21/25.
 */
public class ClassModelLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    private @Nullable Class<? extends BakedModel> clazz;

    public ClassModelLoaderBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(ResourceLocation.fromNamespaceAndPath(MOD_ID, "class"), parent, existingFileHelper, false);
    }

    public ClassModelLoaderBuilder<T> clazz(Class<? extends BakedModel> clazz) {
        try {
            Constructor<?> ctor = clazz.getConstructor();
            if (!Modifier.isPublic(ctor.getModifiers())) {
                throw new IllegalArgumentException("Expected single no-args public constructor.");
            }
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Expected single no-args public constructor.", ex);
        }
        this.clazz = clazz;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        super.toJson(json);
        json.addProperty("class", requireNonNull(clazz).getName());
        return json;
    }
}
