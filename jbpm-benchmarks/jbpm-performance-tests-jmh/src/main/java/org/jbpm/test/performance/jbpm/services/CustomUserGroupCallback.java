package org.jbpm.test.performance.jbpm.services;

import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.services.TestUserGroupCallback;

import java.util.List;

public class CustomUserGroupCallback implements TestUserGroupCallback {

    private final JBossUserGroupCallbackImpl userGroupCallback;

    public CustomUserGroupCallback(String location) {
        userGroupCallback = new JBossUserGroupCallbackImpl(location);
    }

    public CustomUserGroupCallback() {
        userGroupCallback = new JBossUserGroupCallbackImpl("classpath:/usergroups.properties");
    }

    @Override
    public boolean existsUser(String user) {
        return userGroupCallback.existsUser(user);
    }

    @Override
    public boolean existsGroup(String group) {
        return userGroupCallback.existsGroup(group);
    }

    @Override
    public List<String> getGroupsForUser(String user) {
        return userGroupCallback.getGroupsForUser(user);
    }

    @Override
    public void setUserGroups(String user, List<String> groups) {
        // not implemented
    }
}
