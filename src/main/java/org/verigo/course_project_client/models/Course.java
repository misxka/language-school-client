package org.verigo.course_project_client.models;

import java.math.BigDecimal;
import java.util.Set;

public class Course {
    private Integer id;

    private String title;

    private BigDecimal price;

    private String language;

    private String level;

    private boolean isOnline;

    private Set<Lesson> lessons;

    private Set<CourseGroup> groups;


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public Set<CourseGroup> getGroups() {
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

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void setGroups(Set<CourseGroup> groups) {
        this.groups = groups;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
