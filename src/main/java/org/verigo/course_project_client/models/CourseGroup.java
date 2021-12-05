package org.verigo.course_project_client.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseGroup {
    private Integer id;

    private String name;

    private Course course;

    private List<User> participants = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public Course getCourse() {
        return course;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void addParticipants(User user) {
        this.participants.add(user);
    }

    public void setName(String name) {
        this.name = name;
    }
}
