package org.example;

import java.util.Map;

public interface ApplicationContext {

    <T> T getBean(Class<T> type);

    <T> T getBean(String name, Class<T> type);

    <T> Map<String, T > getAllBeans(Class<T> beanType);
}
