package com.rbkmoney.magista.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BeanUtil {

    public static <T> void merge(T source, T target, String... ignoreProperties) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        List<String> ignoreList = Optional.ofNullable(ignoreProperties)
                .map(properties -> Arrays.asList(properties))
                .orElse(Collections.emptyList());

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(source.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null && !ignoreList.contains(propertyDescriptor.getName())) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null) {
                    try {
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        if (readMethod.invoke(target) == null) {
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        }
                    } catch (Throwable ex) {
                        throw new FatalBeanException(
                                "Could not copy property '" + propertyDescriptor.getName() + "' from source to target", ex);
                    }
                }
            }
        }
    }

}
