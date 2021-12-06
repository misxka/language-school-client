package org.verigo.course_project_client.models;

public class UserTaskResult {
    UserTaskKey id;

    private User user;

    private Task task;

    private int points;

    private boolean isCompleted;

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

    public boolean getIsCompleted() {
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

    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }
}
