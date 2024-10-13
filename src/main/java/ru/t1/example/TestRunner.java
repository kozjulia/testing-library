package ru.t1.example;

import ru.t1.annotation.AfterSuite;
import ru.t1.annotation.AfterTest;
import ru.t1.annotation.BeforeSuite;
import ru.t1.annotation.BeforeTest;
import ru.t1.annotation.CsvSource;
import ru.t1.annotation.Test;
import ru.t1.aspect.TestAspectForExample;
import ru.t1.exception.TestException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Класс, который запускает "прогон тестов"
 *
 * @author YKozlova
 */
public class TestRunner {

    private TestRunner() {
    }

    public static void runTests(Class<?> c) {

        TestAspectForExample.checkIfBeforeTest();
        TestAspectForExample.checkIfCsvSource();
        TestAspectForExample.checkIfAfterTest();
        TestAspectForExample.checkIfBeforeSuite();
        TestAspectForExample.checkIfAfterSuite();
        TestAspectForExample.checkIfTest();

        Method[] methods = c.getMethods();

        runTests(methods, c);
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
                    }

                    if (test.isAnnotationPresent(CsvSource.class)) {

                        String value = test.getAnnotation(CsvSource.class).value();
                        String[] values = value.split(",");

                        int a = Integer.parseInt(values[0].trim());
                        String b = values[1].trim();
                        int c = Integer.parseInt(values[2].trim());
                        boolean d = Boolean.parseBoolean(values[3].trim());

                        test.invoke(object, a, b, c, d);
                    } else {

                        test.invoke(object);
                    }

                    if (Objects.nonNull(afterTestMethod)) {
                        afterTestMethod.invoke(object);
                    }
                } catch (Exception exception) {

                    throw new TestException(exception.getMessage());
                }
            }
        }
    }
}
