package ru.t1.aspect;

import lombok.experimental.UtilityClass;
import ru.t1.annotation.AfterSuite;
import ru.t1.annotation.AfterTest;
import ru.t1.annotation.BeforeSuite;
import ru.t1.annotation.BeforeTest;
import ru.t1.annotation.CsvSource;
import ru.t1.annotation.Test;
import ru.t1.example.Example;
import ru.t1.exception.TestException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YKozlova
 */
@UtilityClass
public final class TestAspectForExample {

    public static void checkIfTest() {

        Set<Integer> testQueue = new HashSet<>();

        for (Method method : getMethodsExampleClass()) {
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

    public static void checkIfCsvSource() {

        for (Method method : getMethodsExampleClass()) {
            if (method.isAnnotationPresent(CsvSource.class)) {

                String value = method.getAnnotation(CsvSource.class).value();
                String[] values = value.split(",");

                try {

                    Integer.parseInt(values[0].trim());
                    values[1].trim();
                    Integer.parseInt(values[2].trim());
                    Boolean.parseBoolean(values[3].trim());
                } catch (Exception exception) {

                    throw new TestException("В метод с аннотацией @CsvSource передан некорректный параметр.");
                }
            }
        }

        System.out.println("Методы с аннотацией @CsvSource проверены и являются корректными.");
    }

    public static void checkIfBeforeTest() {

        for (Method method : getMethodsExampleClass()) {
            if (method.isAnnotationPresent(BeforeTest.class) && Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @BeforeTest не может быть static.");
            }
        }

        System.out.println("Методы с аннотацией @BeforeTest проверены и являются корректными.");
    }

    public static void checkIfAfterTest() {

        for (Method method : getMethodsExampleClass()) {
            if (method.isAnnotationPresent(AfterTest.class) && Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @AfterTest не может быть static.");
            }
        }

        System.out.println("Методы с аннотацией @AfterTest проверены и являются корректными.");
    }

    public static void checkIfBeforeSuite() {

        for (Method method : getMethodsExampleClass()) {
            if (method.isAnnotationPresent(BeforeSuite.class) && !Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @BeforeSuite должен быть static.");
            }
        }

        System.out.println("Методы с аннотацией @BeforeSuite проверены и являются корректными.");
    }

    public static void checkIfAfterSuite() {

        for (Method method : getMethodsExampleClass()) {
            if (method.isAnnotationPresent(AfterSuite.class) && !Modifier.isStatic(method.getModifiers())) {

                throw new TestException("Метод с аннотацией @AfterSuite должен быть static.");
            }
        }

        System.out.println("Методы с аннотацией @AfterSuite проверены и являются корректными.");
    }

    private static Method[] getMethodsExampleClass() {

        Class<Example> clazz = Example.class;
        return clazz.getDeclaredMethods();
    }
}
