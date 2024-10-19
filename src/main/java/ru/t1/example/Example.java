package ru.t1.example;

import ru.t1.annotation.AfterSuite;
import ru.t1.annotation.AfterTest;
import ru.t1.annotation.BeforeSuite;
import ru.t1.annotation.BeforeTest;
import ru.t1.annotation.CsvSource;
import ru.t1.annotation.Test;

/**
 * @author YKozlova
 */
public class Example {


    @BeforeTest
    public void exampleBeforeTest() {

        System.out.println("run BeforeTest");
    }

    @AfterTest
    public void exampleAfterTest() {

        System.out.println("run AfterTest");
    }

    @BeforeSuite
    public static void exampleBeforeSuite() {

        System.out.println("run BeforeSuite");
    }

    @AfterSuite
    public static void exampleAfterSuite() {

        System.out.println("run AfterSuite");
    }

    @Test(priority = 10)
    public void exampleTest10() {

        System.out.println("run exampleTest10");
    }

    @Test
    public void exampleTestDefault5() {

        System.out.println("run exampleTestDefault5");
    }

    @Test(priority = 1)
    public void exampleTest1() {

        System.out.println("run exampleTest1");
    }

    @CsvSource("10,Java,20,true")
    public void exampleCsvSource(int a, String b, int c, boolean d) {

        System.out.println("run exampleCsvSource");
    }
}
