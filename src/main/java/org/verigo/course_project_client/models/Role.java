package org.verigo.course_project_client.models;

import org.verigo.course_project_client.constraints.ROLE;

public class Role {
    private ROLE id;

    public Role(ROLE id) {
        this.id = id;
    }

    public ROLE getId() {
        return id;
    }
}
