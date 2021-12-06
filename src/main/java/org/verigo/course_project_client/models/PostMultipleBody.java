package org.verigo.course_project_client.models;

import java.util.List;

public class PostMultipleBody {
    private List<Integer> taskIds;
    private int userId;

    public PostMultipleBody(int userId, List<Integer> taskIds) {
        this.userId = userId;
        this.taskIds = taskIds;
    }


    public int getUserId() {
        return userId;
    }

    public List<Integer> getTaskIds() {
        return taskIds;
    }
}
