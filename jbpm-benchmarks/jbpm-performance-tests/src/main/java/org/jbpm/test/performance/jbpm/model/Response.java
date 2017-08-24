package org.jbpm.test.performance.jbpm.model;

import java.io.Serializable;

public class Response implements Serializable {

    private static final long serialVersionUID = -3803154270051215845L;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
