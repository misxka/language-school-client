package org.verigo.course_project_client.controllers.teacher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WorkspaceViewController {
    @FXML
    private TableView coursesTable;

    @FXML
    public void initialize() {
        getAllCourses();
    }

    private ArrayList<Course> getAllCourses() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("http://localhost:8080/courses/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type coursesListType = new TypeToken<ArrayList<Course>>(){}.getType();
            ArrayList<Course> res = new Gson().fromJson(apiResponse.getBody().toString(), coursesListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
