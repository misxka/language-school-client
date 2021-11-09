package org.verigo.course_project_client.models;

import java.util.Date;

public class User {
//    private final int id;
    private final String login;
    private final String surname;
    private final String name;
    private final Date createdAt;
    private final Date updatedAt;
    private final Role role;
    private final String password;

    public User(String login, String surname, String name, Date createdAt, Date updatedAt, Role role) {
//        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.password = "";
    }

    public User(String login, String surname, String name, Date createdAt, Date updatedAt, Role role, String password) {
//        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

//    public int getId() {
//        return id;
//    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
