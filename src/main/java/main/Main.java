package main;

import main.Utility.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 5555;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("Новый клиент подключился: "
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort());

                // Запускаем обработчик клиента в отдельном потоке
                ClientThread clientThread = new ClientThread(socket);
                new Thread(clientThread).start();
            }

        } catch (IOException e) {
            System.err.println("Ошибка работы сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}