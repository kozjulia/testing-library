package ru.t1.example;

import lombok.experimental.UtilityClass;
import ru.t1.annotation.AfterSuite;
import ru.t1.annotation.AfterTest;
import ru.t1.annotation.BeforeSuite;
import ru.t1.annotation.BeforeTest;
import ru.t1.annotation.CsvSource;
import ru.t1.annotation.Test;
import ru.t1.aspect.TestAspect;
import ru.t1.exception.TestException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Класс, который запускает "прогон тестов"
 *
 * @author YKozlova
 */
@UtilityClass
public final class TestRunner {

    public static void runTests(Class<?> c) {

        checkTestsAnnotation(c);

        Method[] methods = c.getDeclaredMethods();

        runTests(methods, c);
    }

    private static void checkTestsAnnotation(Class<?> clz) {

        Class<TestAspect> clazz = TestAspect.class;
        Method[] methods = clazz.getDeclaredMethods();

        try {

            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    method.invoke(TestAspect.class, clz);
                }
            }
        } catch (Exception exception) {

            throw new TestException(exception.getMessage());
        }
    }

    private static void runTests(Method[] methods, Class<?> c) {

        int counterBeforeTest = 0;
        int counterAfterTest = 0;
        int counterBeforeSuite = 0;
        int counterAfterSuite = 0;
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        Method beforeTestMethod = null;
        Method afterTestMethod = null;
        Method[] tests = new Method[10];
        List<Method> csvSourceTests = new ArrayList<>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeTest.class)) {
                beforeTestMethod = method;
                counterBeforeTest++;
            }
            if (method.isAnnotationPresent(AfterTest.class)) {
                afterTestMethod = method;
                counterAfterTest++;
            }
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuiteMethod = method;
                counterBeforeSuite++;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                afterSuiteMethod = method;
                counterAfterSuite++;
            }
            if (method.isAnnotationPresent(Test.class)) {
                tests[method.getAnnotation(Test.class).priority() - 1] = method;
            }
            if (method.isAnnotationPresent(CsvSource.class)) {
                csvSourceTests.add(method);
            }
        }

        testCounters(counterBeforeTest, counterAfterTest, counterBeforeSuite, counterAfterSuite);

        try {

            Object object = c.getDeclaredConstructor().newInstance();
            if (Objects.nonNull(beforeSuiteMethod)) {
                beforeSuiteMethod.invoke(object);
            }
            runTestsOneByOne(Arrays.stream(tests).toList(), beforeTestMethod, afterTestMethod, object);
            runTestsOneByOne(csvSourceTests, beforeTestMethod, afterTestMethod, object);
            if (Objects.nonNull(afterSuiteMethod)) {
                afterSuiteMethod.invoke(object);
            }
        } catch (Exception exception) {

            throw new TestException(exception.getMessage());
        }
    }

    private static void testCounters(
            int counterBeforeTest,
            int counterAfterTest,
            int counterBeforeSuite,
            int counterAfterSuite
    ) {

        if (counterBeforeTest > 1) {
            throw new TestException("Методов с аннотацией BeforeTest должно быть не больше одного, но их " + counterBeforeTest);
        }
        if (counterAfterTest > 1) {
            throw new TestException("Методов с аннотацией AfterTest должно быть не больше одного, но их " + counterAfterTest);
        }
        if (counterBeforeSuite > 1) {
            throw new TestException("Методов с аннотацией BeforeSuite должно быть не больше одного, но их " + counterBeforeSuite);
        }
        if (counterAfterSuite > 1) {
            throw new TestException("Методов с аннотацией AfterSuite должно быть не больше одного, но их " + counterAfterSuite);
        }
    }

    private static void runTestsOneByOne(
            List<Method> tests,
            Method beforeTestMethod,
            Method afterTestMethod,
            Object object
    ) {

        for (Method test : tests) {
            if (Objects.nonNull(test)) {

                try {
                    if (Objects.nonNull(beforeTestMethod)) {
                        beforeTestMethod.invoke(object);
                        System.out.println("Успешно отработал метод " + beforeTestMethod.getName());
                    }

                    if (test.isAnnotationPresent(CsvSource.class)) {

                        String value = test.getAnnotation(CsvSource.class).value();
                        String[] values = value.split(",");
                        Object[] args = new Object[values.length];

                        Parameter[] parameters = test.getParameters();
                        for (int i = 0; i < parameters.length; i++) {
                            if (parameters[i].getType().equals(Integer.class)
                                    || parameters[i].getType().equals(int.class)) {
                                args[i] = Integer.parseInt(values[i]);
                                continue;
                            }
                            if (parameters[i].getType().equals(Boolean.class)
                                    || parameters[i].getType().equals(boolean.class)) {
                                args[i] = Boolean.parseBoolean(values[i]);
                                continue;
                            }

                            args[i] = values[i];
                        }

                        test.invoke(object, args);
                        System.out.println("Успешно отработал метод " + test.getName());
                    } else {

                        test.invoke(object);
                        System.out.println("Успешно отработал метод " + test.getName());
                    }

                    if (Objects.nonNull(afterTestMethod)) {
                        afterTestMethod.invoke(object);
                        System.out.println("Успешно отработал метод " + afterTestMethod.getName());
                    }
                } catch (Exception exception) {

                    throw new TestException(exception.getMessage());
                }
            }
        }
    }
}
