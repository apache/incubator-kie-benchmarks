package org.jbpm.test.performance.jbpm;

import org.kie.api.task.UserGroupCallback;

import java.util.*;

public class GeneratingUserGroupCallback implements UserGroupCallback {

    private final Set<String> users;
    private final Set<String> groups;
    private final String originalGroup;

    public GeneratingUserGroupCallback(final String baseName, final String group, final int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive integer");
        }

        if (isEmptyStr(group)) {
            throw new IllegalArgumentException("group must be non-empty string");
        }

        if (isEmptyStr(baseName)) {
            throw new IllegalArgumentException("baseName must be non-empty string");
        }

        originalGroup = group;
        groups = new HashSet<String>(2);
        groups.add(group);
        groups.add("Administrators");

        users = new HashSet<String>(count + 1);
        for (int i = 0; i < count; i++) {
            users.add(String.format("%s_%d", baseName, i));
        }
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    private boolean isEmptyStr(final String str) {
        return str == null || "".equals(str.trim());
    }

    @Override
    public boolean existsUser(String user) {
        return users.contains(user);
    }

    @Override
    public boolean existsGroup(String group) {
        return this.groups.contains(group);
    }

    @Override
    public List<String> getGroupsForUser(String user) {
        List<String> groupsForUser = new LinkedList<String>();
        if (users.contains(user)) {
            groupsForUser.add(originalGroup);
        }
        return groupsForUser;
    }
}