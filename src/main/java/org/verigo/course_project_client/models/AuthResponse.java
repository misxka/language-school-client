package org.verigo.course_project_client.models;

public class AuthResponse {
    private final String message;
    private final User user;

    public AuthResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
