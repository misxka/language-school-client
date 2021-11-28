package org.verigo.course_project_client.models;

import java.util.Set;

public class Task {
    private Integer id;

    private String title;

    private String description;

    private boolean isHometask;
    private String isHometaskString;

    private int maxPoints;
    private String maxPointsString;

    private Lesson lesson;

    private Set<UserTaskResult> usersResults;


    public Task() {

    }

    public Task(String title) {
        this.title = title;
        this.description = "";
        this.isHometaskString = "";
        this.maxPointsString = "";
    }

    public Task(Task task) {
        this.title = task.title;
        this.description = task.description;
        this.isHometaskString = String.valueOf(task.isHometask);
        this.maxPointsString = String.valueOf(task.maxPoints);
    }

    public Task(String title, String description, boolean isHometask, int maxPoints) {
        this.title = title;
        this.description = description;
        this.isHometaskString = String.valueOf(isHometask);
        this.maxPointsString = String.valueOf(maxPoints);
    }



    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsHometask() {
        return isHometask;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public Set<UserTaskResult> getUsersResults() {
        return usersResults;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsHometask(boolean isHometask) {
        this.isHometask = isHometask;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public void setUsersResults(Set<UserTaskResult> usersResults) {
        this.usersResults = usersResults;
    }


    public String getIsHometaskString() {
        return isHometaskString;
    }

    public String getMaxPointsString() {
        return maxPointsString;
    }

    public void setIsHometaskString(String isHometaskString) {
        this.isHometaskString = isHometaskString;
    }

    public void setMaxPointsString(String maxPointsString) {
        this.maxPointsString = maxPointsString;
    }
}
