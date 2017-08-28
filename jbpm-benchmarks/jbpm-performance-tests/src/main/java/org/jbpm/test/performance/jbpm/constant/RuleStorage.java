package org.jbpm.test.performance.jbpm.constant;

public enum RuleStorage {

    ValidationUserFact("ValidationUserFact.drl"), ;

    private String path;

    private RuleStorage(String name) {
        this.path = "rules/" + name;
    }

    public String getPath() {
        return path;
    }

}
