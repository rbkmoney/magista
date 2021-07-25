package com.rbkmoney.magista.config;

import com.rbkmoney.magista.exception.IoException;
import com.rbkmoney.magista.exception.NoSuchFileException;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringApplicationPropertiesLoader {

    public static Properties loadFromSpringApplicationPropertiesFile(List<String> keys) {
        var fileProperties = loadPropertiesByFile();
        var filtered = fileProperties.entrySet().stream()
                .filter(entry -> keys.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat(filtered.keySet()).containsAll(keys);
        var properties = new Properties();
        properties.putAll(filtered);
        return properties;
    }

    private static Map<String, Object> loadPropertiesByFile() {
        try {
            var currentClass = SpringApplicationPropertiesLoader.class;
            var parameters = findPropertiesFileParameters(currentClass);
            var classPathResource = new ClassPathResource(parameters.getName(), currentClass.getClassLoader());
            //noinspection unchecked
            return ((Map<String, OriginTrackedValue>) parameters.getPropertySourceLoader().get()
                    .load(classPathResource.getFilename(), classPathResource)
                    .get(0)
                    .getSource())
                    .entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException ex) {
            throw new IoException("Error when loading properties, ", ex);
        }
    }

    private static PropertiesFileParameters findPropertiesFileParameters(
            Class<SpringApplicationPropertiesLoader> currentClass) {
        if (currentClass.getResource("/application.yml") != null) {
            return PropertiesFileParameters.builder()
                    .propertySourceLoader(YamlPropertySourceLoader::new)
                    .name("application.yml")
                    .build();
        } else if (currentClass.getResource("/application.properties") != null) {
            return PropertiesFileParameters.builder()
                    .propertySourceLoader(PropertiesPropertySourceLoader::new)
                    .name("application.properties")
                    .build();
        } else if (currentClass.getResource("/application.xml") != null) {
            return PropertiesFileParameters.builder()
                    .propertySourceLoader(PropertiesPropertySourceLoader::new)
                    .name("application.xml")
                    .build();
        } else {
            throw new NoSuchFileException("Error on load src/main/resources/application.[yml|properties|xml] — " +
                    "file not found");
        }
    }

    @Data
    @Builder
    private static class PropertiesFileParameters {

        private Supplier<PropertySourceLoader> propertySourceLoader;
        private String name;

    }
}
