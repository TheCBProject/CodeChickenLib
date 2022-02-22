package codechicken.lib.reflect;

import com.google.common.base.Objects;
import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.commons.Remapper;

import java.util.Optional;
import java.util.function.BiFunction;

@Deprecated // TODO Remove in 1.17
public class ObfMapping {

    public static Remapper remapper = new FMLRemapper();

    public String s_owner;
    public String s_name;
    public String s_desc;

    public ObfMapping(String owner) {
        this(owner, "", "");
    }

    public ObfMapping(String owner, String name) {
        this(owner, name, "");
    }

    public ObfMapping(String owner, String name, String desc) {
        this.s_owner = owner;
        this.s_name = name;
        this.s_desc = desc;

        if (s_owner.contains(".")) {
            throw new IllegalArgumentException(s_owner);
        }
    }

    public ObfMapping(ObfMapping descmap, String subclass) {
        this(subclass, descmap.s_name, descmap.s_desc);
    }

    public static ObfMapping fromDesc(String s) {
        int lastDot = s.lastIndexOf('.');
        if (lastDot < 0) {
            return new ObfMapping(s, "", "");
        }
        int sep = s.indexOf('(');//methods
        int sep_end = sep;
        if (sep < 0) {
            sep = s.indexOf(' ');//some stuffs
            sep_end = sep + 1;
        }
        if (sep < 0) {
            sep = s.indexOf(':');//fields
            sep_end = sep + 1;
        }
        if (sep < 0) {
            return new ObfMapping(s.substring(0, lastDot), s.substring(lastDot + 1), "");
        }

        return new ObfMapping(s.substring(0, lastDot), s.substring(lastDot + 1, sep), s.substring(sep_end));
    }

    public ObfMapping subclass(String subclass) {
        return new ObfMapping(this, subclass);
    }

    public boolean isClass(String name) {
        return name.replace('.', '/').equals(s_owner);
    }

    public boolean matches(String name, String desc) {
        return s_name.equals(name) && s_desc.equals(desc);
    }

    public String javaClass() {
        return s_owner.replace('/', '.');
    }

    public String methodDesc() {
        return s_owner + "." + s_name + s_desc;
    }

    public String fieldDesc() {
        return s_owner + "." + s_name + ":" + s_desc;
    }

    public boolean isClass() {
        return s_name.length() == 0;
    }

    public boolean isMethod() {
        return s_desc.contains("(");
    }

    public boolean isField() {
        return !isClass() && !isMethod();
    }

    public ObfMapping map(Remapper mapper) {
        if (mapper == null) {
            return this;
        }

        if (isMethod()) {
            s_name = mapper.mapMethodName(s_owner, s_name, s_desc);
        } else if (isField()) {
            s_name = mapper.mapFieldName(s_owner, s_name, s_desc);
        }

        s_owner = mapper.mapType(s_owner);

        if (isMethod()) {
            s_desc = mapper.mapMethodDesc(s_desc);
        } else if (s_desc.length() > 0) {
            s_desc = mapper.mapDesc(s_desc);
        }

        return this;
    }

    public ObfMapping remap() {
        map(remapper);
        return this;
    }

    public ObfMapping copy() {
        return new ObfMapping(s_owner, s_name, s_desc);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObfMapping)) {
            return false;
        }

        ObfMapping desc = (ObfMapping) obj;
        return s_owner.equals(desc.s_owner) && s_name.equals(desc.s_name) && s_desc.equals(desc.s_desc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(s_desc, s_name, s_owner);
    }

    @Override
    public String toString() {
        if (s_name.length() == 0) {
            return "[" + s_owner + "]";
        }
        if (s_desc.length() == 0) {
            return "[" + s_owner + "." + s_name + "]";
        }
        return "[" + (isMethod() ? methodDesc() : fieldDesc()) + "]";
    }

    private static class FMLRemapper extends Remapper {

        private static Optional<BiFunction<INameMappingService.Domain, String, String>> nameFunction = FMLLoader.getNameFunction("mcp");

        @Override
        public String mapMethodName(String owner, String name, String descriptor) {
            return nameFunction.map(f -> f.apply(INameMappingService.Domain.METHOD, name)).orElse(name);
        }

        @Override
        public String mapFieldName(String owner, String name, String descriptor) {
            return nameFunction.map(f -> f.apply(INameMappingService.Domain.FIELD, name)).orElse(name);
        }

        @Override
        public String map(String typeName) {
            return nameFunction.map(f -> f.apply(INameMappingService.Domain.CLASS, typeName)).orElse(typeName);
        }
    }
}
