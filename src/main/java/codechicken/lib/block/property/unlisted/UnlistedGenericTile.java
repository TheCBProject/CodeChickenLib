package codechicken.lib.block.property.unlisted;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by covers1624 on 3/08/2017.
 */
public class UnlistedGenericTile<T extends TileEntity> extends UnlistedPropertyBase<T> {

	private final Class<T> tileClass;

	public UnlistedGenericTile(String name, Class<T> tileClass) {
		super(name);
		this.tileClass = tileClass;
	}

	@Override
	public Class<T> getType() {
		return tileClass;
	}

	@Override
	public String valueToString(T value) {
		return value.toString();
	}
}
