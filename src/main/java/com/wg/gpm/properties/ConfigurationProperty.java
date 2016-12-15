package com.wg.gpm.properties;

/**
 * Created by aidan on 15/12/16.
 */
public enum ConfigurationProperty implements Property {
    REQUIRED_SENDER("requiredsender"),
    REPOSITORY_URL("repoUrl"),
    WEBSITE_ROOT_URL("webRootUrl"),
    PUSH_TO_REMOTE("pushToRemote"),
    GIT_TOKEN("gittoken"),
    GIT_DIRECTORY("gitdir"),
    GMAIL_TOKEN_LOCATION("gmailtoken");

    private final String key;

    ConfigurationProperty(String key) {
        this.key = key;
    }

    @Override
    public String getPropertyKey() {
        return key;
    }
}
