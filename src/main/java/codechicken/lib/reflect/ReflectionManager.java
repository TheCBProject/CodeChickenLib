package codechicken.lib.reflect;

import org.objectweb.asm.Type;

import java.lang.reflect.*;

public class ReflectionManager {

    private static Field modifiersField;

    /**
     * Checks if a Method or Field contains the static bit.
     *
     * @param modifiers The Method or Fields modifier bits.
     * @return If modifiers contains a bit a Modifier.STATIC
     */
    @Deprecated
    public static boolean isStatic(int modifiers) {
        return (modifiers & Modifier.STATIC) != 0;
    }

    /**
     * Checks if a method is static.
     *
     * @param method The method.
     * @return If the method is static.
     */
    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * Checks if a field is static.
     *
     * @param field The field.
     * @return If the field is static.
     */
    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * Finds a class.
     *
     * @param mapping The ObfMapping to find a class for.
     * @param init    if {@code true} the class will be initialized.
     *                See Section 12.4 of <em>The Java Language Specification</em>.
     * @return The class. Null if the class does not exist.
     */
    public static Class<?> findClass(ObfMapping mapping, boolean init) {
        try {
            return Class.forName(mapping.javaClass(), init, ReflectionManager.class.getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a class.
     * Defaults to initialize the class.
     *
     * @param mapping The mapping to find a class for.
     * @return The class. Null if the class does nto exist.
     */
    public static Class<?> findClass(ObfMapping mapping) {
        return findClass(mapping, true);
    }

    /**
     * Checks if a class exists.
     *
     * @param mapping The mapping to check.
     * @return If the class exists.
     */
    public static boolean classExists(ObfMapping mapping) {
        return findClass(mapping, false) != null;
    }

    /**
     * Finds a class.
     *
     * @param name The name of the class.
     * @return The class, Null if the class does not exist.
     */
    public static Class<?> findClass(String name) {
        return findClass(new ObfMapping(name.replace(".", "/")), true);
    }

    /**
     * Sets a field.
     *
     * @param mapping  The mapping.
     * @param instance The Class instance holding the field, May be null for static classes.
     * @param value    The value to set in the field.
     */
    public static void setField(ObfMapping mapping, Object instance, Object value) {
        try {
            Field field = getField(mapping);
            field.setAccessible(true);
            removeFinal(field);
            field.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls a method.
     *
     * @param mapping    The mapping.
     * @param returnType The return type of the method you are invoking.
     * @param instance   The instance of the class containing the method, May be null for static classes.
     * @param params     Any method parameters the method requires.
     * @param <R>        The return type.
     * @return Anything returned from the method.
     */
    public static <R> R callMethod(ObfMapping mapping, Class<R> returnType, Object instance, Object... params) {
        try {
            return callMethod_Unsafe(mapping, returnType, instance, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is Unsafe and will throw exceptions.
     * Calls a method.
     *
     * @param mapping    The mapping.
     * @param returnType The return type of the method you are invoking.
     * @param instance   The instance of the class containing the method, May be null for static classes.
     * @param params     Any method parameters the method requires.
     * @param <R>        The return type.
     * @return Anything returned from the method.
     * @throws InvocationTargetException if the underlying method throws an exception.
     * @throws IllegalAccessException    if the method is inaccessible.
     */
    @SuppressWarnings ("unchecked")
    public static <R> R callMethod_Unsafe(ObfMapping mapping, Class<R> returnType, Object instance, Object... params) throws InvocationTargetException, IllegalAccessException {
        mapping.remap();
        Class<?> clazz = findClass(mapping);
        Method method = null;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(mapping.s_name) && Type.getMethodDescriptor(m).equals(mapping.s_desc)) {
                method = m;
                break;
            }
        }
        if (method != null) {
            method.setAccessible(true);
            return (R) method.invoke(instance, params);
        }
        return null;
    }

    /**
     * Invokes a Classes constructor.
     * The mapping is used to provide both the constructors descriptor and the class name.
     * The mappings method name doesn't really matter, but the proper name is "<init>"
     *
     * @param mapping    The mapping.
     * @param returnType The return type of the constructor.
     * @param params     The parameters the constructor requires.
     * @param <R>        The return type.
     * @return The new instance.
     */
    public static <R> R newInstance(ObfMapping mapping, Class<R> returnType, Object... params) {
        try {
            return newInstance_Unsafe(mapping, returnType, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is Unsafe and will throw exceptions.
     * Invokes a Classes constructor.
     * The mapping is used to provide both the constructors descriptor and the class name.
     * The mappings method name doesn't really matter, but the proper name is "<init>"
     *
     * @param mapping    The mapping.
     * @param returnType The return type of the constructor.
     * @param params     The parameters the constructor requires.
     * @param <R>        The return type.
     * @return The new instance.
     */
    @SuppressWarnings ("unchecked")
    //TODO, if !returnType.isAssignableFrom(clazz) throw exception
    public static <R> R newInstance_Unsafe(ObfMapping mapping, Class<R> returnType, Object... params) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<?> clazz = findClass(mapping);
        Constructor<?> constructor = null;
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (Type.getConstructorDescriptor(c).equals(mapping.s_desc)) {
                constructor = c;
                break;
            }
        }

        if (constructor != null) {
            constructor.setAccessible(true);
            return (R) constructor.newInstance(params);
        }
        return null;
    }

    /**
     * Checks if a field exists.
     *
     * @param mapping The mapping.
     * @return If the field exists.
     */
    public static boolean hasField(ObfMapping mapping) {
        try {
            getField_Unsafe(mapping);
            return true;
        } catch (NoSuchFieldException nfe) {
            return false;
        }
    }

    /**
     * Gets a fields value.
     *
     * @param mapping  The mapping.
     * @param instance The Class instance holding the field, May be null for static classes.
     * @param clazz    The fields type.
     * @param <R>      The return type.
     * @return The fields value.
     */
    @SuppressWarnings ("unchecked")
    public static <R> R getField(ObfMapping mapping, Object instance, Class<R> clazz) {
        try {
            Field field = getField(mapping);
            return (R) field.get(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a field.
     *
     * @param mapping The mapping.
     * @return The field.
     */
    public static Field getField(ObfMapping mapping) {
        mapping.remap();

        try {
            return getField_Unsafe(mapping);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is Unsafe and will throw exceptions.
     * Gets a field.
     *
     * @param mapping The mapping.
     * @return The field.
     * @throws NoSuchFieldException If the field does not exist.
     */
    public static Field getField_Unsafe(ObfMapping mapping) throws NoSuchFieldException {
        mapping.remap();

        Class<?> clazz = findClass(mapping);
        Field field = clazz.getDeclaredField(mapping.s_name);
        field.setAccessible(true);
        removeFinal(field);
        return field;
    }

    /**
     * Removes the final modifier from a field allowing you to set final fields.
     *
     * @param field The field to remove the final modifer for.
     */
    public static void removeFinal(Field field) {

        if ((field.getModifiers() & Modifier.FINAL) == 0) {
            return;
        }
        try {
            if (modifiersField == null) {
                modifiersField = getField(new ObfMapping("java/lang/reflect/Field", "modifiers"));
                modifiersField.setAccessible(true);
            }
            modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
