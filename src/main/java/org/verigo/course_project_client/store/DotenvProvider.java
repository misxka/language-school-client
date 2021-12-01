package org.verigo.course_project_client.store;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvProvider {
    private Dotenv dotenv;
    private final static DotenvProvider INSTANCE = new DotenvProvider();

    private DotenvProvider() {
        dotenv = Dotenv.configure().load();
    }

    public static DotenvProvider getInstance() {
        return INSTANCE;
    }

    public void setDotenv(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public Dotenv getDotenv() {
        return this.dotenv;
    }
}
