package codechicken.lib.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.objectweb.asm.tree.ClassNode;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * This is added as a class transformer if CodeChickenCore is installed. Adding it as a class transformer will speed evaluation up slightly by automatically caching superclasses when they are first loaded.
 */
public class ClassHeirachyManager implements IClassTransformer
{
    public static HashSet<String> knownClasses = new HashSet<String>();
    public static HashMap<String, ArrayList<String>> superclasses = new HashMap<String, ArrayList<String>>();
    private static LaunchClassLoader cl = (LaunchClassLoader)ClassHeirachyManager.class.getClassLoader();
    
    static
    {
        cl.addTransformerExclusion("codechicken.lib.asm");
    }
    
    public static String toKey(String name)
    {
        if(ObfMapping.obfuscated)
            name = FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/','.');
        return name;
    }
    
    /**
     * Returns true if clazz extends, either directly or indirectly, superclass.
     * @param name The class in question
     * @param superclass The class being extended
     * @param bytes The bytes for the class. Only needed if not already defined.
     * @return
     */
    public static boolean classExtends(String name, String superclass, byte[] bytes)
    {
        name = toKey(name);
        
        if(!knownClasses.contains(name))
            new ClassHeirachyManager().transform(name, name, bytes);
        
        return classExtends(name, superclass);
    }
    
    public static boolean classExtends(String clazz, String superclass)
    {        
        if(clazz.equals(superclass))
            return true;
        
        if(clazz.equals("java.lang.Object"))
            return false;
        
        declareClass(clazz);
        
        if(!superclasses.containsKey(clazz))//just can't handle this
            return false;
        
        for(String s : superclasses.get(clazz))
            if(classExtends(s, superclass))
                return true;
        
        return false;
    }

    private static void declareClass(String name) 
    {
        name = toKey(name);
        
        try
        {
            if(!knownClasses.contains(name))
            {
                try
                {
                    byte[] bytes = cl.getClassBytes(name);
                    if(bytes != null)
                        new ClassHeirachyManager().transform(name, name, bytes);
                }
                catch(Exception e)
                {
                }
                
                if(!knownClasses.contains(name))
                {
                    Class<?> aclass = Class.forName(name);
                    
                    knownClasses.add(name);
                    if(aclass.isInterface())
                        addSuperclass(name, "java.lang.Object");
                    else
                        addSuperclass(name, aclass.getSuperclass().getName());
                    for(Class<?> iclass : aclass.getInterfaces())
                        addSuperclass(name, iclass.getName());
                }
            }
        }
        catch(ClassNotFoundException e)
        {
        }
    }

    @Override
    public byte[] transform(String name, String tname, byte[] bytes)
    {
        if (bytes == null) return null;
        if(!knownClasses.contains(name))
        {
            ClassNode node = ASMHelper.createClassNode(bytes);
            
            knownClasses.add(name);
            addSuperclass(name, node.superName.replace('/', '.'));
            for(String iclass : node.interfaces)
                addSuperclass(name, iclass.replace('/', '.'));
        }
        
        return bytes;
    }

    private static void addSuperclass(String name, String superclass)
    {
        ArrayList<String> supers = superclasses.get(name);
        if(supers == null)
            superclasses.put(name, supers = new ArrayList<String>());
        supers.add(superclass);
        supers.add(new ObfMapping(superclass.replace('.', '/')).toRuntime().javaClass());
    }

    public static String getSuperClass(String name, boolean runtime) 
    {
        name = toKey(name);
        declareClass(name);
        
        if(!knownClasses.contains(name))
            return "java.lang.Object";
        
        return superclasses.get(name).get(runtime ? 1 : 0);
    }
}
