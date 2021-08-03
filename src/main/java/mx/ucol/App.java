package mx.ucol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import mx.ucol.handlers.RootHandler;
import mx.ucol.handlers.TodosHandler;
import mx.ucol.helpers.DBConnection;

public class App {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);

        // Create the ToDos table if not exists
        Connection connection = DBConnection.getInstance();
        String sql = "CREATE TABLE IF NOT EXISTS todos (" + "id integer PRIMARY KEY," + "title text NOT NULL,"
                + "completed integer NOT NULL);";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error on App.main executeUpdate: " + e.getMessage());
        }

        server.createContext("/", new RootHandler());
        server.createContext("/api/v1/todos", new TodosHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }
}
