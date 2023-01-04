package com.manning.apisecurityinaction;

import com.manning.apisecurityinaction.controller.SpaceController;
import org.dalesbred.Database;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var datasource = JdbcConnectionPool.create(
                "jdbc:h2:mem:natter", "natter", "password");
        var database = Database.forDataSource(datasource);
        createTables(database);

        var spaceController = new SpaceController(database);
        post("/spaces", spaceController::createSpace);

        after((request, response) -> {
            response.type("application/json");
        });

        internalServerError(new JSONObject().put("error", "internal server error").toString());
        notFound(new JSONObject().put("error", "not found").toString());
    }

    private static void createTables(Database database) throws IOException, URISyntaxException {
        var path = Paths.get(Main.class.getResource("/schema.sql").toURI());
        database.update(Files.readString(path));
    }

}