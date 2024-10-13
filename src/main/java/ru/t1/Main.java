package ru.t1;

import ru.t1.example.Example;
import ru.t1.example.TestRunner;

/**
 * @author YKozlova
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("Запускается TestRunner...");
        TestRunner.runTests(Example.class);
        System.out.println("TestRunner отработал.");
    }
}
