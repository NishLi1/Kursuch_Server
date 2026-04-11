package main;

import main.Server.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int PORT_NUMBER = 5555;
    private static ServerSocket serverSocket;
    private static ClientThread clientHandler;
    private static Thread thread;
    private static final List<Socket> currentSockets = new ArrayList<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("✅ Сервер успешно запущен на порту " + PORT_NUMBER);

            while (true) {
                // Очистка закрытых сокетов (безопасно)
                currentSockets.removeIf(Socket::isClosed);

                Socket socket = serverSocket.accept();
                currentSockets.add(socket);

                System.out.println("Клиент подключился: " +
                        socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                clientHandler = new ClientTread(socket);
                thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}