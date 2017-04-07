package codechicken.lib.asm.proxy;

import codechicken.lib.util.ReflectionManager;
import com.google.common.base.Throwables;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by covers1624 on 7/04/2017.
 */
public class ProxyInjector {

    private static Logger logger = LogManager.getLogger("CodeChickenLib Proxy Injection");

    public static void runInjector(ASMDataTable table) {

        Set<ASMData> targets = table.getAll(FunctionProxy.class.getName());
        ModClassLoader classLoader = Loader.instance().getModClassLoader();

        for (ASMData data : targets) {
            try {
                Class<?> proxyTarget = Class.forName(data.getClassName(), true, classLoader);
                Field target = proxyTarget.getDeclaredField(data.getObjectName());
                if (target == null) {
                    throw new RuntimeException(String.format("Attempted to load proxy into field %s.%s but field does not exist..", data.getClassName(), data.getObjectName()));
                }
                target.setAccessible(true);
                FunctionProxy annotation = target.getAnnotation(FunctionProxy.class);
                String targetClass = getInjectClass(annotation.injectClassCallback(), data, classLoader);
                Object proxyInstance = Class.forName(targetClass, true, classLoader).newInstance();

                if (!target.getType().isAssignableFrom(proxyInstance.getClass())) {
                    throw new RuntimeException(String.format("Attempted to load incompatible class type to proxy field!, Field Type: %s, Inject Type %s, Field %s.%s", target.getType().getName(), targetClass, data.getClassName(), data.getObjectName()));
                }
                target.set(null, proxyInstance);
                logger.debug("Successfully injected proxy to field %s.%s", data.getClassName(), data.getObjectName());

            } catch (Exception e) {
                logger.fatal("Fatal Exception thrown whilst loading proxy for class %s.%s", e, data.getClassName(), data.getObjectName());
                Throwables.propagate(e);
            }
        }
    }

    private static String getInjectClass(String test, ASMData data, ClassLoader classLoader) throws Exception {
        String className;
        String methodName;
        if (!test.equals("")) {
            int index = test.lastIndexOf('.');
            className = test.substring(0, index);
            methodName = test.substring(index + 1);

        } else {
            className = data.getClassName();
            methodName = "proxyCallback";
        }
        Class<?> clazz = Class.forName(className, true, classLoader);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName) && method.getReturnType().equals(String.class) && ReflectionManager.isStatic(method)) {
                method.setAccessible(true);
                return (String) method.invoke(null);
            }
        }
        throw new RuntimeException(String.format("Unable to find static method with string return type! Method: %s.%s", className, methodName));
    }

}
