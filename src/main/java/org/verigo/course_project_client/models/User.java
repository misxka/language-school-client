package org.verigo.course_project_client.models;

import java.util.*;

public class User {
    private Integer id;
    private String login;
    private String surname;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private Role role;
    private String password;
    private List<CourseGroup> groups = new ArrayList<>();
    private List<UserTaskResult> tasksResults = new ArrayList<>();


    public User() {

    }

    public User(Integer id, String login, String surname, String name, Date createdAt, Date updatedAt, Role role, List<CourseGroup> groups, List<UserTaskResult> tasksResults) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.password = "";
        this.groups = groups;
        this.tasksResults = tasksResults;
    }

    public User(Integer id, String login, String surname, String name, Date createdAt, Date updatedAt, Role role, String password, List<CourseGroup> groups, List<UserTaskResult> tasksResults) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.password = password;
        this.groups = groups;
        this.tasksResults = tasksResults;
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


    public List<CourseGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CourseGroup> groups) {
        this.groups = groups;
    }

    public List<UserTaskResult> getTasksResults() {
        return tasksResults;
    }

    public void setTasksResults(List<UserTaskResult> tasksResults) {
        this.tasksResults = tasksResults;
    }
}
