package org.verigo.course_project_client.controllers.student;

import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.models.CourseGroup;

import java.util.List;

public class ChooseGroupViewController {
    List<CourseGroup> groups;

    public void setGroups(List<CourseGroup> groups) {
        this.groups = groups;
    }
}
