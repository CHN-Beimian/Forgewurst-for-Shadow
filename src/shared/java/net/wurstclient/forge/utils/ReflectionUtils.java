package net.wurstclient.forge.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
	/**
     * ��ȡ˽�г�Ա������ֵ
     * @param instance
     * @param filedName
     * @return
     */
    public static Object getPrivateField(Object instance, String filedName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(instance);
    }
 
    /**
     * ����˽�г�Ա��ֵ
     * @param instance
     * @param fieldName
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setPrivateField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
 
    /**
     * ����˽�з���
     * @param instance
     * @param methodName
     * @param classes
     * @param objects
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokePrivateMethod(Object instance, String methodName, Class[] classes, String objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(instance, objects);
    }
	/*
	 * public static void setObfuscatedField(Object instance, String fieldName,
	 * Object value) throws NoSuchFieldException, IllegalAccessException{ Field
	 * field = instance.getClass().getDeclaredField(fieldName); }
	 */
}
