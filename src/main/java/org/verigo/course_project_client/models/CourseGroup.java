package org.verigo.course_project_client.models;

import java.util.HashSet;
import java.util.Set;

public class CourseGroup {
    private Integer id;

    private Course course;

    private Set<User> participants = new HashSet<>();



    public Integer getId() {
        return id;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public Course getCourse() {
        return course;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public void addParticipants(User user) {
        this.participants.add(user);
    }
}
