package org.jbpm.test.performance.kieserver.constant;

public enum UserStorage {

    PerfUser("perfUser", "perfUser1234;"), EngUser("engUser", "engUser1234;");

    private String userId;
    private String password;

    private UserStorage(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

}
