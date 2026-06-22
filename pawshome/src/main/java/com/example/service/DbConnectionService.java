package com.example.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class DbConnectionService {

    private final DataSource dataSource;

    public DbConnectionService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isConnected() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(3);
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatus() {
        try (Connection conn = dataSource.getConnection()) {
            String db = conn.getCatalog();
            String url = conn.getMetaData().getURL();
            return "Conectado a: " + db + " | Servidor: " + url;
        } catch (Exception e) {
            return "Error de conexión: " + e.getMessage();
        }
    }
}
