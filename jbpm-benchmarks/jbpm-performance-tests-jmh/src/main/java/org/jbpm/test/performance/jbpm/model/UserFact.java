package org.jbpm.test.performance.jbpm.model;

import java.io.Serializable;

public class UserFact implements Serializable {

    private static final long serialVersionUID = -7364964864697675450L;

    private String id;

    private int age;

    public UserFact() {

    }

    public UserFact(String id, int age) {
        this.id = id;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
