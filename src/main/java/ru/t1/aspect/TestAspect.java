package ru.t1.aspect;

import lombok.experimental.UtilityClass;
import ru.t1.annotation.AfterSuite;
import ru.t1.annotation.AfterTest;
import ru.t1.annotation.BeforeSuite;
import ru.t1.annotation.BeforeTest;
import ru.t1.annotation.CsvSource;
import ru.t1.annotation.Test;
import ru.t1.exception.TestException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YKozlova
 */
@UtilityClass
public final class TestAspect {

    public static void checkIfTest(Class<?> clazz) {

        Set<Integer> testQueue = new HashSet<>();

        for (Method method : getMethodsClass(clazz)) {
            if (method.isAnnotationPresent(Test.class)) {

                int priority = method.getAnnotation(Test.class).priority();
                if (priority < 1 || priority > 10) {
                    throw new TestException("Параметр priority аннотации @Test должен быть в пределах от 1 до 10 включительно.");
                }

                if (Modifier.isStatic(method.getModifiers())) {
                    throw new TestException("Метод с аннотацией @Test не может быть static.");
                }

                if (testQueue.contains(priority)) {
                    throw new TestException("Параметр priority аннотации @Test не может повторяться: " + priority);
                } else {
                    testQueue.add(priority);
                }
            }
        }

        System.out.println("Методы с аннотацией @Test проверены и являются корректными.");
    }

    public static void checkIfCsvSource(Class<?> clazz) {

        for (Method method : getMethodsClass(clazz)) {
            if (method.isAnnotationPresent(CsvSource.class)) {

                String value = method.getAnnotation(CsvSource.class).value();
                String[] values = value.split(",");

                try {

                    if (values.length != method.getParameterCount()) {
                        throw new TestException("В метод с аннотацией @CsvSource передан некорректный параметр.");
                    }

                    Parameter[] parameters = method.getParameters();
                    for (int i = 0; i < parameters.length; i++) {

                        if (parameters[i].getType().equals(Integer.class)
                                || parameters[i].getType().equals(int.class)) {
                            Integer.parseInt(values[i]);
                        }
                        if (parameters[i].getType().equals(Boolean.class)
                                || parameters[i].getType().equals(boolean.class)) {
                            Boolean.parseBoolean(values[i]);
                        }
                    }
                } catch (Exception exception) {

                    throw new TestException("В метод с аннотацией @CsvSource передан некорректный параметр.");
                }
            }
        }

        System.out.println("Методы с аннотацией @CsvSource проверены и являются корректными.");
    }

    public static void checkIfBeforeTest(Class<?> clazz) {

        for (Method method : getMethodsClass(clazz)) {
            if (method.isAnnotationPresent(BeforeTest.class) && Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @BeforeTest не может быть static.");
            }
        }

        System.out.println("Методы с аннотацией @BeforeTest проверены и являются корректными.");
    }

    public static void checkIfAfterTest(Class<?> clazz) {

        for (Method method : getMethodsClass(clazz)) {
            if (method.isAnnotationPresent(AfterTest.class) && Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @AfterTest не может быть static.");
            }
        }

        System.out.println("Методы с аннотацией @AfterTest проверены и являются корректными.");
    }

    public static void checkIfBeforeSuite(Class<?> clazz) {

        for (Method method : getMethodsClass(clazz)) {
            if (method.isAnnotationPresent(BeforeSuite.class) && !Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @BeforeSuite должен быть static.");
            }
        }

        System.out.println("Методы с аннотацией @BeforeSuite проверены и являются корректными.");
    }

    public static void checkIfAfterSuite(Class<?> clazz) {

        for (Method method : getMethodsClass(clazz)) {
            if (method.isAnnotationPresent(AfterSuite.class) && !Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @AfterSuite должен быть static.");
            }
        }

        System.out.println("Методы с аннотацией @AfterSuite проверены и являются корректными.");
    }

    private static Method[] getMethodsClass(Class<?> clazz) {

        return clazz.getDeclaredMethods();
    }
}
