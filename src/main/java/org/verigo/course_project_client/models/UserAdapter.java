package org.verigo.course_project_client.models;

import org.verigo.course_project_client.constraints.ROLE;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

//TODO Try to refactor UserAdapter to User so there's no need in UserAdapter

public class UserAdapter {
    private final int id;
    private String login;
    private String surname;
    private String name;
    private final Date createdAt;
    private Date updatedAt;
    private final String password;
    private ROLE roleName;
    private String roleNameAdapted;

    private static List<String> roles = Arrays.asList( "Администратор", "Учитель", "Студент" );

    public UserAdapter(User user, ROLE roleName) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.password = user.getPassword();
        this.roleName = roleName;
        this.roleNameAdapted = roles.get(roleName.ordinal());
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

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoleName(ROLE roleName) {
        this.roleName = roleName;
    }

    public void setRoleName(String roleNameAdapted) {
        int index = roles.indexOf(roleNameAdapted);
        if(index == ROLE.ADMIN.ordinal()) this.roleName = ROLE.ADMIN;
        if(index == ROLE.TEACHER.ordinal()) this.roleName = ROLE.TEACHER;
        if(index == ROLE.STUDENT.ordinal()) this.roleName = ROLE.STUDENT;
    }

    public void setRoleNameAdapted(String roleNameAdapted) {
        this.roleNameAdapted = roleNameAdapted;
        this.setRoleName(roleNameAdapted);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
