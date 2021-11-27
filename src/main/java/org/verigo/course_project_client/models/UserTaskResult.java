package org.verigo.course_project_client.models;

public class UserTaskResult {
    UserTaskKey id;

    private User user;

    private Task task;

    private int points;

    private boolean isCompleted;

    public UserTaskResult() {

    }

    public UserTaskResult(UserTaskKey id, User user, Task task) {
        this.id = id;
        this.user = user;
        this.task = task;
        this.isCompleted = false;
    }

    public UserTaskResult(UserTaskKey id, User user, Task task, int points) {
        this.id = id;
        this.user = user;
        this.task = task;
        this.points = points;
        this.isCompleted = true;
    }


    public Task getTask() {
        return task;
    }

    public User getUser() {
        return user;
    }

    public int getPoints() {
        return points;
    }

    public UserTaskKey getId() {
        return id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setId(UserTaskKey id) {
        this.id = id;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
