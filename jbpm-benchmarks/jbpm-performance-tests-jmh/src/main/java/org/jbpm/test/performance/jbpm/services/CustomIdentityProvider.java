package org.jbpm.test.performance.jbpm.services;

import org.jbpm.test.services.TestIdentityProvider;

import java.util.ArrayList;
import java.util.List;

public class CustomIdentityProvider implements TestIdentityProvider {

    private String name = "perfUser";
    private List<String> roles = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    @Override
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void reset() {
        name = "perfUser";
        roles = new ArrayList<>();
    }
}
