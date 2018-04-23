package com.excilys.gradureau.computer_database.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyFileUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyFileUtility.class);

    private PropertyFileUtility() {
    }

    public static Properties readPropertyFile(final String filepath) {
        final Properties databaseConnectionProperties = new Properties();
        final InputStream path = ClassLoader.getSystemResourceAsStream(filepath);
        try {
            databaseConnectionProperties.load(path);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return databaseConnectionProperties;
    }
}
