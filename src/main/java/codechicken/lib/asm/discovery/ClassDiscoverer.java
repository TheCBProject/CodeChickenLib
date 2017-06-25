package codechicken.lib.asm.discovery;

import codechicken.lib.asm.CCLCorePlugin;
import codechicken.lib.util.CommonUtils;
import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Deprecated//USE ANNOTATIONS!!
public class ClassDiscoverer {

    public Predicate<String> matcher;
    public String[] superclasses;
    public ArrayList<Class<?>> classes;
    public ModClassLoader modClassLoader;

    public ClassDiscoverer(Predicate<String> matcher, Class<?>... superclasses) {
        this.matcher = matcher;
        this.superclasses = new String[superclasses.length];
        for (int i = 0; i < superclasses.length; i++) {
            this.superclasses[i] = superclasses[i].getName().replace('.', '/');
        }

        classes = new ArrayList<>();
        modClassLoader = Loader.instance().getModClassLoader();
    }

    public ClassDiscoverer(Class<?>... superclasses) {
        this(test -> true, superclasses);
    }

    public ArrayList<Class<?>> findClasses() {
        try {
            findClasspathMods();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classes;
    }

    private void checkAddClass(String resource) {
        try {
            String classname = resource.replace(".class", "").replace("\\", ".").replace("/", ".");
            byte[] bytes = Launch.classLoader.getClassBytes(classname);
            if (bytes == null) {
                return;
            }

            ClassReader reader = new ClassReader(bytes);
            List<String> interfaces = Lists.newArrayList(reader.getSuperName());
            Collections.addAll(interfaces, reader.getInterfaces());

            for (String superclass : superclasses) {
                if (!interfaces.contains(superclass)) {
                    return;
                }
            }

            addClass(classname);
        } catch (IOException e) {
            CCLCorePlugin.logger.error("Unable to load class: " + resource, e);
        }
    }

    private void addClass(String classname) {
        try {
            Class<?> class1 = Class.forName(classname, true, modClassLoader);
            classes.add(class1);
        } catch (Throwable t) {
            CCLCorePlugin.logger.error("Unable to load class: " + classname, t);
        }
    }

    private void findClasspathMods() {
        List<ModContainer> mods = Loader.instance().getActiveModList();
        HashSet<String> searched = new HashSet<>();
        for (ModContainer mod : mods) {
            File source = mod.getSource();
            if (source == null || searched.contains(source.getAbsolutePath())) {
                continue;
            }
            searched.add(source.getAbsolutePath());

            if (source.isFile()) {
                CCLCorePlugin.logger.debug("Found a mod container %s, examining for codechicken classes", source.getAbsolutePath());
                try {
                    readFromZipFile(source);
                } catch (Exception e) {
                    CCLCorePlugin.logger.error("Failed to scan " + source.getAbsolutePath() + ", the zip file is invalid", e);
                }
            } else if (source.isDirectory()) {
                CCLCorePlugin.logger.debug("Found a minecraft related directory at %s, examining for codechicken classes", source.getAbsolutePath());
                readFromDirectory(source, source);
            }
        }
    }

    private void readFromZipFile(File file) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(file);
        ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
        do {
            ZipEntry zipentry = zipinputstream.getNextEntry();
            if (zipentry == null) {
                break;
            }
            String fullname = zipentry.getName().replace('\\', '/');
            int pos = fullname.lastIndexOf('/');
            String name = pos == -1 ? fullname : fullname.substring(pos + 1);
            if (!zipentry.isDirectory() && matcher.test(name)) {
                checkAddClass(fullname);
            }
        } while (true);
        fileinputstream.close();
    }

    private void readFromDirectory(File directory, File basedirectory) {
        for (File child : directory.listFiles()) {
            if (child.isDirectory()) {
                readFromDirectory(child, basedirectory);
            } else if (child.isFile() && matcher.test(child.getName())) {
                String fullname = CommonUtils.getRelativePath(basedirectory, child);
                checkAddClass(fullname);
            }
        }
    }
}
