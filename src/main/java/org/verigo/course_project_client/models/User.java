package org.verigo.course_project_client.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User {
    private Integer id;
    private String login;
    private String surname;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private Role role;
    private String password;
    private Set<CourseGroup> groups = new HashSet<>();
    private Set<UserTaskResult> tasksResults;


    public User() {

    }

    public User(Integer id, String login, String surname, String name, Date createdAt, Date updatedAt, Role role, Set<CourseGroup> groups, Set<UserTaskResult> tasksResults) {
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

    public User(Integer id, String login, String surname, String name, Date createdAt, Date updatedAt, Role role, String password, Set<CourseGroup> groups, Set<UserTaskResult> tasksResults) {
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


    public Set<CourseGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<CourseGroup> groups) {
        this.groups = groups;
    }

    public Set<UserTaskResult> getTasksResults() {
        return tasksResults;
    }

    public void setTasksResults(Set<UserTaskResult> tasksResults) {
        this.tasksResults = tasksResults;
    }
}
