package org.verigo.course_project_client.models;

import java.util.List;
import java.util.Set;

public class Lesson {
    private Integer id;

    private String title;

    private List<Task> tasks;

    private Course course;


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Course getCourse() {
        return course;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
