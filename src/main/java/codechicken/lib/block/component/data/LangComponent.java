package codechicken.lib.block.component.data;

import codechicken.lib.datagen.LanguageProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * a data generator component for defining Localizations.
 * <p>
 * Created by covers1624 on 29/8/22.
 *
 * @see DataGenComponent
 */
@ApiStatus.Experimental
public class LangComponent extends DataGenComponent {

    private final String locale;
    private final Supplier<String> key;
    private final String name;

    /**
     * Construct a new {@link LangComponent} using the default description id of
     * the provided block as the language key.
     * <p>
     * This defaults to the {@code en_us} locale.
     *
     * @param block The block.
     * @param name  The localization.
     */
    public LangComponent(Block block, String name) {
        this("en_us", block::getDescriptionId, name);
    }

    /**
     * Constructs a new {@link LangComponent}.
     *
     * @param key  The localization key.
     * @param name The localization value.
     */
    public LangComponent(String key, String name) {
        this("en_us", key, name);
    }

    /**
     * Constructs a new {@link LangComponent}.
     *
     * @param locale The locale of this LangComponent.
     * @param key    The localization key.
     * @param name   The localization value.
     */
    public LangComponent(String locale, String key, String name) {
        this(locale, () -> key, name);
    }

    protected LangComponent(String locale, Supplier<String> key, String name) {
        this.locale = locale;
        this.key = key;
        this.name = name;
    }

    @Override
    protected void addToProvider(DataProvider provider) {
        if (provider instanceof LanguageProvider p && p.getLocale().equals(locale)) {
            p.add(key.get(), name);
        }
    }
}
