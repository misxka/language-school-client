package org.verigo.course_project_client.models;

import java.util.Date;

public class User {
    private final Integer id;
    private final String login;
    private final String surname;
    private final String name;
    private final Date createdAt;
    private final Date updatedAt;
    private final Role role;
    private final String password;

    public User(Integer id, String login, String surname, String name, Date createdAt, Date updatedAt, Role role) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.password = "";
    }

    public User(Integer id, String login, String surname, String name, Date createdAt, Date updatedAt, Role role, String password) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.password = password;
    }

    public User(UserAdapter userAdapter) {
        this.id = userAdapter.getId();
        this.login = userAdapter.getLogin();
        this.surname = userAdapter.getSurname();
        this.name = userAdapter.getName();
        this.createdAt = userAdapter.getCreatedAt();
        this.updatedAt = userAdapter.getUpdatedAt();
        this.password = userAdapter.getPassword();
        this.role = new Role(userAdapter.getRoleName());
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

    public Integer getId() {
        return id;
    }

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
