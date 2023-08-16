package br.cepel.questdb.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import br.cepel.questdb.services.properties.DbProperties;
import io.questdb.client.Sender;

public abstract class QuestDBService {
    private static Semaphore semaphore = new Semaphore(1); 
    private boolean hasContention = false;

    private Sender sender = null;
    private Connection connection = null;

    public QuestDBService(boolean hasContention) {
        this.hasContention = hasContention;
    }

    public QuestDBService() {
        this(false);
    }

    private void createSender() {
        if (sender == null) sender = DbProperties.getInfluxSender();
    }

    protected Sender getSender() {
        createSender();
        return sender;
    }

    protected void createConnection() throws SQLException {
        if (connection == null) connection = DbProperties.getPostgresConnection();
    }

    protected Connection getConnection() throws SQLException {
        createConnection();
        return connection;
    }

    public boolean acquire() {
        if (!hasContention) return true;
        try {
            semaphore.acquire();
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void release() {
        if (!hasContention) return;
        semaphore.release();
    }

    public void flush() {
        if (sender != null) sender.flush();
    }

    public void flushAndClose() {
        if (sender != null) {
            sender.flush();
            sender.close();
            sender = null;
        }
    }
}
