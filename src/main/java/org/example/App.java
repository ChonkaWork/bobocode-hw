package org.example;


import org.example.testTemporary.SomeClass;

import java.util.Map;

public class App {

    private App() {
    }

    public static void main(String[] args) {
        var context = new AnnotationApplicationContext("org.example");

        var someClassFromContext = context.getBean(SomeClass.class);

        Map<String, SomeClass> allBeans = context.getAllBeans(SomeClass.class);

        allBeans.entrySet().forEach(System.out::println);

        someClassFromContext.printMessage();
    }

}
