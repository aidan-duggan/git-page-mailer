package com.wg.gpm.properties;

import org.apache.commons.configuration2.Configuration;

/**
 * Created by aidan on 15/12/16.
 */
public class DefaultConfigurationAccess implements ConfigurationAccess{

    private final Configuration configuration;

    public DefaultConfigurationAccess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getPropertyValue(Property property) {
        return configuration.getString(property.getPropertyKey());
    }
}
