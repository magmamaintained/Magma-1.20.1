package io.izzel.arclight.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EnumHelper {
    private static final long[] ENUM_CACHE_OFFSETS;

    public EnumHelper() {
    }

    public static <T> T addEnum(Class<T> cl, String name, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            Unsafe.ensureClassInitialized(cl);
            Field field = cl.getDeclaredField("$VALUES");
            Object base = Unsafe.staticFieldBase(field);
            long offset = Unsafe.staticFieldOffset(field);
            T[] arr = (T[]) Unsafe.getObject(base, offset);
            T[] newArr = (T[]) Array.newInstance(cl, arr.length + 1);
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            T newInstance = makeEnum(cl, name, arr.length, ctorTypes, ctorParams);
            newArr[arr.length] = newInstance;
            Unsafe.putObject(base, offset, newArr);
            reset(cl);
            return newInstance;
        } catch (Throwable var11) {
            var11.printStackTrace();
            return null;
        }
    }

    public static <T> void addEnums(Class<T> cl, List<T> list) {
        try {
            Field field = cl.getDeclaredField("$VALUES");
            Object base = Unsafe.staticFieldBase(field);
            long offset = Unsafe.staticFieldOffset(field);
            T[] arr = (T[]) Unsafe.getObject(base, offset);
            T[] newArr = (T[]) Array.newInstance(cl, arr.length + list.size());
            System.arraycopy(arr, 0, newArr, 0, arr.length);

            for(int i = 0; i < list.size(); ++i) {
                newArr[arr.length + i] = list.get(i);
            }

            Unsafe.putObject(base, offset, newArr);
            reset(cl);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public static <T> T makeEnum(Class<T> cl, String name, int i, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            Unsafe.ensureClassInitialized(cl);
            List<Class<?>> ctor = new ArrayList(ctorTypes.size() + 2);
            ctor.add(String.class);
            ctor.add(Integer.TYPE);
            ctor.addAll(ctorTypes);
            MethodHandle constructor = Unsafe.lookup().findConstructor(cl, MethodType.methodType(Void.TYPE, ctor));
            List<Object> param = new ArrayList(ctorParams.size() + 2);
            param.add(name);
            param.add(i);
            param.addAll(ctorParams);
            return (T) constructor.invokeWithArguments(param);
        } catch (Throwable var8) {
            var8.printStackTrace();
            return null;
        }
    }

    private static void reset(Class<?> cl) {
        long[] var1 = ENUM_CACHE_OFFSETS;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            long offset = var1[var3];
            Unsafe.putObjectVolatile(cl, offset, (Object)null);
        }

    }

    static {
        List<Long> offsets = new ArrayList();
        String[] var1 = new String[]{"enumConstantDirectory", "enumConstants", "enumVars"};
        int i = var1.length;

        for(int var3 = 0; var3 < i; ++var3) {
            String s = var1[var3];

            try {
                Field field = Class.class.getDeclaredField(s);
                offsets.add(Unsafe.objectFieldOffset(field));
            } catch (NoSuchFieldException var6) {
            }
        }

        if (offsets.isEmpty()) {
            throw new IllegalStateException("Unable to find offsets for Enum");
        } else {
            long[] arr = new long[offsets.size()];

            for(i = 0; i < arr.length; ++i) {
                arr[i] = (Long)offsets.get(i);
            }

            ENUM_CACHE_OFFSETS = arr;
        }
    }
}
