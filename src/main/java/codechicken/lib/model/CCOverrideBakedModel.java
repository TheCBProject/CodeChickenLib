package codechicken.lib.model;

/**
 * Created by covers1624 on 7/25/2016.
 * Default implementation for CCOverrideListHandler.
 */
public class CCOverrideBakedModel extends SimpleOverrideBakedModel {
    public CCOverrideBakedModel() {
        super(new CCOverrideListHandler());
    }
}
