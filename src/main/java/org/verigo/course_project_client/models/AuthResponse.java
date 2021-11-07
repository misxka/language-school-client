package org.verigo.course_project_client.models;

import org.verigo.course_project_client.constraints.ROLE;

public class AuthResponse {
    private final String message;
    private final ROLE role;

    public AuthResponse(String message, ROLE role) {
        this.message = message;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public ROLE getRole() {
        return role;
    }
}
