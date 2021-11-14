package org.verigo.course_project_client.models;

import org.verigo.course_project_client.constraints.ROLE;

import java.util.Date;


public class UserAdapter {
    private final int id;
    private final String login;
    private final String surname;
    private final String name;
    private final Date createdAt;
    private final Date updatedAt;
    private final ROLE roleName;
    private final String roleNameAdapted;

    private static String[] roles = { "Администратор", "Учитель", "Студент" };

    public UserAdapter(User user, ROLE roleName) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.roleName = roleName;
        this.roleNameAdapted = roles[roleName.ordinal()];
    }

    public String getRoleNameAdapted() {
        return roleNameAdapted;
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
