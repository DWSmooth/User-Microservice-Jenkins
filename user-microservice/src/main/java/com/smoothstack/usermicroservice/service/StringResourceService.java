package com.smoothstack.usermicroservice.service;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Hashtable;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class StringResourceService {
    private Hashtable<String, String> cacheTable;
    private ResourceLoader resourceLoader;

    public StringResourceService() {
        cacheTable = new Hashtable<>();
        resourceLoader = new DefaultResourceLoader();
    }

    // Loads the resource at the specified classpath and returns it as a String.
    // Loading is lazy and repeat calls will read from an internal cache.
    public String get(String classpath) {
        if (cacheTable.containsKey(classpath))
            return cacheTable.get(classpath);

        // Load and save to cache
        Resource resource = resourceLoader.getResource(classpath);
        String value = resourceToString(resource);
        cacheTable.put(classpath, value);

        return value;
    }

    // Returns an unchecked exception as this is indicative of a program typo.
    private static String resourceToString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
