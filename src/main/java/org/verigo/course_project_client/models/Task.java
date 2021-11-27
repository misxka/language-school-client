package org.verigo.course_project_client.models;

import java.util.Set;

public class Task {
    private Integer id;

    private String title;

    private String description;

    private boolean isHometask;

    private int maxPoints;

    private Lesson lesson;

    private Set<UserTaskResult> usersResults;


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHometask() {
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

    public void setHometask(boolean hometask) {
        isHometask = hometask;
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
}
