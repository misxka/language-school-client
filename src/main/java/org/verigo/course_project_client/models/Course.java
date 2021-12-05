package org.verigo.course_project_client.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class Course {
    private Integer id;

    private String title;

    private BigDecimal price;

    private String language;

    private String level;

    private boolean isOnline;

    private List<Lesson> lessons;

    private List<CourseGroup> groups;


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<CourseGroup> getGroups() {
        return groups;
    }

    public String getLanguage() {
        return language;
    }

    public String getLevel() {
        return level;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setIsOnline(boolean online) {
        isOnline = online;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void setGroups(List<CourseGroup> groups) {
        this.groups = groups;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
