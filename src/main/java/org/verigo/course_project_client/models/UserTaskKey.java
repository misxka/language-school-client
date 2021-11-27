package org.verigo.course_project_client.models;

public class UserTaskKey {
    private Integer userId;

    private Integer taskId;

    public UserTaskKey(Integer userId, Integer taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }

    public UserTaskKey() {

    }


    public Integer getUserId() {
        return userId;
    }

    public Integer getTaskId() {
        return taskId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
