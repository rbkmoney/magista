package com.rbkmoney.magista.util;

import com.google.common.base.CaseFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class BeanUtil {

    public static Map<String, String> toStringMap(Object bean) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(bean.getClass());
        Map<String, String> stringMap = new HashMap<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && !"class".equals(propertyDescriptor.getName())) {
                try {
                    Object value = readMethod.invoke(bean);
                    if (value != null) {
                        stringMap.put(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propertyDescriptor.getName()),
                                value instanceof LocalDateTime ? ((LocalDateTime) value).toInstant(ZoneOffset.UTC).toString() : String.valueOf(value)
                        );
                    }
                } catch (Throwable ex) {
                    throw new FatalBeanException(
                            "Could not read property value '" + propertyDescriptor.getName() + "' from source to target", ex);
                }
            }
        }
        return stringMap;
    }

    public static <T> void merge(T source, T target, String... ignoreProperties) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        List<String> ignoreList = Optional.ofNullable(ignoreProperties)
                .map(Arrays::asList)
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
