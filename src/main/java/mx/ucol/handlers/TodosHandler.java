package mx.ucol.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import mx.ucol.models.Todo;
import mx.ucol.helpers.DBConnection;
import mx.ucol.helpers.JSON;

public class TodosHandler implements HttpHandler {
    Gson gson = null;
    Integer cod = null;
    String text = "";
    private static Connection connection = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        gson = new GsonBuilder().setPrettyPrinting().create();

        String url = "jdbc:sqlite:resources/todos.db";

        try {
            connection = DriverManager.getConnection(url);
            stmt = connection.createStatement();
        } catch (SQLException e) {
            System.err.println("Error on DBConnection: " + e.getMessage());
        }

        switch (requestMethod) {
            case "GET":
                getHandler(exchange);
                break;
            case "POST":
                postHandler(exchange);
                break;
            case "PUT":
                putHandler(exchange);
                break;
            case "DELETE":
                deleteHandler(exchange);
                break;
            default:
                notSupportedHandler(exchange);
                break;
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String json = "{}";

        String uri = exchange.getRequestURI().toString();
        Integer id = null;

        try {
            String idStr = uri.substring(uri.lastIndexOf("/")+1);
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {}

        if(id != null){
            Todo todo = null;
            try{
                rs = stmt.executeQuery("SELECT * FROM todos WHERE id = " + id);

                int _id = -1;
                String title = "";
                int completed = -1;
                while (rs.next()) {
                    _id = rs.getInt("id");
                    title = rs.getString("title");
                    completed = rs.getInt("completed");
                }

                todo = new Todo(_id, title, completed == 1 ? true : false);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            json = gson.toJson(todo);

        }else{
            List<Todo> todos = new ArrayList<Todo>();
            try{
                stmt = connection.createStatement();
                rs = stmt.executeQuery("SELECT  * FROM todos");

                int Id = -1;
                String title = "";
                int completed = -1;
                while (rs.next()) {
                    Id = rs.getInt("id");
                    title = rs.getString("title");
                    completed = rs.getInt("completed");

                    todos.add(new Todo(Id, title, completed == 1? true : false));
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }

            json = gson.toJson(todos);
        }

        OutputStream output = exchange.getResponseBody();


        byte[] response = json.getBytes();

        exchange.sendResponseHeaders(200, response.length);
        output.write(response);
        output.close();
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        cod = 200;
        text = "{'mensaje':'El todo fue creado'}";
        Boolean flag = null;

        InputStreamReader streamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        int buffer;
        StringBuilder builder = new StringBuilder();

        while ((buffer = bufferedReader.read()) != -1) {
            builder.append((char) buffer);
        }

        bufferedReader.close();
        streamReader.close();

        String jsonBody = builder.toString();

        String title = "";
        String completed = "";

        if(jsonBody.contains("\",")){
            String[] body = jsonBody.split(",");
            title = body[0].substring(body[0].lastIndexOf(":\"") + 2, body[0].lastIndexOf("\""));
            completed = body[1].substring(body[1].indexOf(":") + 2, body[1].length() - 1);
        } else if(jsonBody.contains("title"))
            title = jsonBody.substring(jsonBody.indexOf(":") + 2, jsonBody.lastIndexOf("\""));

        title = title.trim();
        completed = completed.trim();

        Todo todo = new Todo(1, title, completed == "true" ? true : false);

        try{
            if(todo.getTitle() == "" || todo.getTitle() == null) flag = false;

            int done = -1;
            boolean _completed = todo.getCompleted();
            String _title = todo.getTitle();

            if(_completed) done = 1;
            else done = 0;

            String query = "INSERT INTO todos (title,completed) VALUES('" + _title + "'," + done + ")";
            stmt.execute(query);

            flag = true;
        }catch (SQLException e){
            e.printStackTrace();
            flag = false;
        }

        if(!flag) {
            cod = 500;
            text = "{'mensaje':'Llene todos los campos'}";
        }

        OutputStream output = exchange.getResponseBody();

        JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

        String json = gson.toJson(jsonObject);
        byte[] response = json.getBytes();

        exchange.sendResponseHeaders(cod, response.length);
        output.write(response);
        output.close();
    }

    private void putHandler(HttpExchange exchange) throws IOException {
        cod = 200;
        text = "{'mensaje':'El todo fue actualizado'}";
        Boolean flag = null;

        String uri = exchange.getRequestURI().toString();
        String idStr = uri.substring(uri.lastIndexOf("/")+1);
        Integer id = Integer.parseInt(idStr);

        InputStreamReader streamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        int buffer;
        StringBuilder builder = new StringBuilder();

        while ((buffer = bufferedReader.read()) != -1) {
            builder.append((char) buffer);
        }

        bufferedReader.close();
        streamReader.close();

        String jsonBody = builder.toString();
        String title = "";
        String completedStr = "";

        if(jsonBody.contains("title"))
            title = jsonBody.substring(jsonBody.indexOf(":") + 3, jsonBody.indexOf("\","));

        if(jsonBody.contains("completed"))
            completedStr = jsonBody.substring(jsonBody.lastIndexOf(":") + 2, jsonBody.lastIndexOf("}") - 1);

        title = title.trim();
        completedStr = completedStr.trim();

        Todo todo = new Todo(1, title, Boolean.parseBoolean(completedStr));

        try{
            int done = -1;
            boolean completed = todo.getCompleted();
            String _title = todo.getTitle();

            if(completed == true)
                done = 1;
            else
                done = 0;

            Todo _todo = null;
            Todo currentTodo = null;
            try{
                rs = stmt.executeQuery("SELECT * FROM todos WHERE id = " + id);

                int _id = -1;
                String __title = "";
                int _completed = -1;
                while (rs.next()) {
                    _id = rs.getInt("id");
                    __title = rs.getString("title");
                    _completed = rs.getInt("completed");
                }

                currentTodo = new Todo(_id, __title, _completed == 1 ? true : false);
            }
            catch (SQLException e){
                e.printStackTrace();
            }

            if(_title == "" || _title == null) _title = currentTodo.getTitle();

            String query = "UPDATE todos SET title = '"+ _title +"', completed = " + done + " WHERE id = " + id;
            stmt.execute(query);
        }catch (SQLException e){
            e.printStackTrace();
            flag = false;
        }
        flag = true;

        if(!flag) {
            cod = 500;
            text = "{'mensaje':'Existió un error'}";
        }

        JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

        OutputStream output = exchange.getResponseBody();

        String json = gson.toJson(jsonObject);
        byte[] response = json.getBytes();

        exchange.sendResponseHeaders(cod, response.length);
        output.write(response);
        output.close();
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        cod = 200;
        text = "{'mensaje':'El todo ha sido eliminado'}";
        String all = "";
        boolean flag = false;
        Integer id = null;

        String uri = exchange.getRequestURI().toString();
        try {
            String idStr = uri.substring(uri.lastIndexOf("/")+1);
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
        }

        all = uri.substring(uri.lastIndexOf("/")+1).trim();

        if(id != null){
            try{
                String query = "DELETE FROM todos WHERE id = " + id;
                stmt.execute(query);
            }catch (SQLException e){
                e.printStackTrace();
                flag = false;
            }
            flag = true;
        }

        if(all.equals("all")){
            try{
                String query = "DELETE FROM todos";
                stmt.execute(query);
            }catch (SQLException e){
                e.printStackTrace();
                flag = false;
            }
            flag = true;

            text = "{'mensaje':'Se han eliminado todos los todos'}";
        }

        if(!flag) {
            cod = 500;
            text = "{'mensaje':'Ha ocurrido un error'}";
        }

        OutputStream output = exchange.getResponseBody();
        byte[] response = text.getBytes();

        exchange.sendResponseHeaders(cod, response.length);
        output.write(response);
        output.close();
    }

    private void notSupportedHandler(HttpExchange exchange) throws IOException {
        OutputStream output = exchange.getResponseBody();
        byte[] response = "Not supported".getBytes();

        exchange.sendResponseHeaders(200, response.length);
        output.write(response);
        output.close();
    }
}
