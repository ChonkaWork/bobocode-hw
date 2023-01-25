package org.example;

import lombok.SneakyThrows;
import org.example.annotation.Autowired;
import org.example.annotation.Bean;
import org.example.exeception.NoSuchBeanException;
import org.example.exeception.NoUniqueBeanException;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AnnotationApplicationContext implements ApplicationContext {

    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public AnnotationApplicationContext(String packageName) {
        init(packageName);
    }

    private void init(String packageName) {
        var reflections = new Reflections(packageName);
        var typesAnnotatedWith = reflections.getTypesAnnotatedWith(Bean.class);
        typesAnnotatedWith.forEach(this::registerBean);
    }

    @Override
    public <T> T getBean(Class<T> type) {
        Map<String, T> allBeans = getAllBeans(type);
        if (allBeans.size() > 1) {
            throw new NoUniqueBeanException();
        }
        return findBeanInContext(type)
                .orElseThrow(NoSuchBeanException::new);
    }

    @Override
    public <T> T getBean(String name, Class<T> type) {
        return context.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(name))
                .filter(type::isInstance)
                .findAny()
                .map(type::cast)
                .orElseThrow(NoSuchBeanException::new);
    }

    @Override
    public <T> Map<String, T> getAllBeans(Class<T> type) {
        return context.entrySet()
                .stream()
                .filter(entry -> type.isInstance(entry.getValue()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> type.cast(entry.getValue())
                        )
                );
    }

    private <T> Optional<T> findBeanInContext(Class<T> type) {
        return context.values()
                .stream()
                .filter(type::isInstance)
                .findAny()
                .map(type::cast);
    }

    private <T> void registerBean(Class<T> type) {
        Object parentBean = addBeanToContext(type);
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                injectBeanToParentBean(parentBean, field);
            }
        }
    }

    @SneakyThrows
    private void injectBeanToParentBean(Object parentBean, Field field) {
        Class<?> fieldType = field.getType();
        Optional<?> beanInContext = findBeanInContext(fieldType);
        Object bean;
        if (beanInContext.isEmpty()) {
            bean = addBeanToContext(fieldType);
        } else {
            bean = beanInContext.get();
        }
        field.setAccessible(true);
        field.set(parentBean, bean);
    }

    @SneakyThrows
    private Object addBeanToContext(Class<?> type) {
        Bean annotation = type.getAnnotation(Bean.class);
        String value = annotation.value();
        Object instance = type.getConstructor().newInstance();
        if (value.length() == 0) {
            String simpleName = type.getSimpleName();
            String transformedClassName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            context.put(transformedClassName, instance);
        } else {
            context.put(value, instance);
        }
        return instance;
    }
}
