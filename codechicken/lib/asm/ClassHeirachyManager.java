package codechicken.lib.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.objectweb.asm.tree.ClassNode;


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
    
    /**
     * Returns true if clazz extends, either directly or indirectly, superclass.
     * @param clazz The class in question
     * @param superclass The class being extended
     * @param bytes The bytes for the clazz. Only needed if not already defined.
     * @return
     */
    public static boolean classExtends(String clazz, String superclass, byte[] bytes)
    {
        if(!knownClasses.contains(clazz))
            new ClassHeirachyManager().transform(clazz, clazz, bytes);
        
        return classExtends(clazz, superclass);
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

    private static void declareClass(String clazz) 
    {
        try
        {
            if(!knownClasses.contains(clazz))
            {
                try
                {
                    byte[] bytes = cl.getClassBytes(clazz);
                    if(bytes != null)
                        new ClassHeirachyManager().transform(clazz, clazz, bytes);
                }
                catch(Exception e)
                {
                }
                
                if(!knownClasses.contains(clazz))
                {
                    Class<?> aclass = Class.forName(clazz);
                    
                    knownClasses.add(clazz);
                    if(aclass.isInterface())
                        addSuperclass(clazz, "java.lang.Object");
                    else
                        addSuperclass(clazz, aclass.getSuperclass().getName());
                    for(Class<?> iclass : aclass.getInterfaces())
                        addSuperclass(clazz, iclass.getName());
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

    public static String getSuperClass(String c) 
    {
        declareClass(c);
        if(!knownClasses.contains(c))
            return "java.lang.Object";
        return superclasses.get(c).get(0);
    }
}
