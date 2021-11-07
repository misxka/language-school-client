package org.verigo.course_project_client.models;

import org.verigo.course_project_client.constraints.ROLE;

import java.util.Date;

public class UsersAdapter {
    private final int id;
    private final String login;
    private final String surname;
    private final String name;
    private final Date createdAt;
    private final Date updatedAt;
    private final ROLE roleName;

    public UsersAdapter(UsersResponse usersResponse, ROLE roleName) {
        this.id = usersResponse.getId();
        this.login = usersResponse.getLogin();
        this.name = usersResponse.getName();
        this.surname = usersResponse.getSurname();
        this.createdAt = usersResponse.getCreatedAt();
        this.updatedAt = usersResponse.getUpdatedAt();
        this.roleName = roleName;
    }

    public ROLE getRoleName() {
        return roleName;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
