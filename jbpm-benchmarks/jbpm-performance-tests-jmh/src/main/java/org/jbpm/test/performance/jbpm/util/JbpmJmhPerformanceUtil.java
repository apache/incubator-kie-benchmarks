package org.jbpm.test.performance.jbpm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class JbpmJmhPerformanceUtil {

    private static final Logger logger = LoggerFactory.getLogger(JbpmJmhPerformanceUtil.class);

    public static final String TEMP_FOLDER = "file://" + System.getProperty("java.io.tmpdir");

    public static Properties readProperties(String propertiesLocation) {
        Properties config = null;
        URL locationUrl = null;

        logger.debug("JMH properties will be loaded from {}", propertiesLocation);
        if (propertiesLocation.startsWith("classpath:")) {
            String stripedLocation = propertiesLocation.replaceFirst("classpath:", "");
            locationUrl = JbpmJmhPerformanceUtil.class.getResource(stripedLocation);
            if (locationUrl == null) {
                locationUrl = Thread.currentThread().getContextClassLoader().getResource(stripedLocation);
            }
        } else {
            try {
                locationUrl = new URL(propertiesLocation);
            } catch (MalformedURLException e) {
                locationUrl = JbpmJmhPerformanceUtil.class.getResource(propertiesLocation);
                if (locationUrl == null) {
                    locationUrl = Thread.currentThread().getContextClassLoader().getResource(propertiesLocation);
                }
            }
        }
        if (locationUrl != null) {
            config = new Properties();
            try (InputStream stream = locationUrl.openStream()) {
                config.load(stream);
            } catch (IOException e) {
                logger.error("Error when loading properties for JMH", e);
                config = null;
            }
        }

        return config;
    }

    public static void writeObjectToFile(Object object, String filename) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new URL(TEMP_FOLDER + File.separator + filename).getPath());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // write object to file
            oos.writeObject(object);
        }
    }

    public static Object readObjectFromFile(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(new URL(TEMP_FOLDER + File.separator + filename).getPath());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // write object to file
            return ois.readObject();
        }
    }
}
