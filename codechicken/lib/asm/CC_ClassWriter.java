package codechicken.lib.asm;

import org.objectweb.asm.ClassWriter;


public class CC_ClassWriter extends ClassWriter
{
    public CC_ClassWriter(int flags)
    {
        super(flags);
    }
    
    @Override
    protected String getCommonSuperClass(String type1, String type2)
    {
        String c = type1.replace('/', '.');
        String d = type2.replace('/', '.');
        if(ClassHeirachyManager.classExtends(d, c))
            return type1;
        if(ClassHeirachyManager.classExtends(c, d))
            return type2;
        do
        {
            c = ClassHeirachyManager.getSuperClass(c);
        }
        while(!ClassHeirachyManager.classExtends(d, c));
        return c.replace('.', '/');
    }
}
