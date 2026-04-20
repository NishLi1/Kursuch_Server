package main.Utility;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * Хэширует пароль с помощью BCrypt (с солью)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        // 12 — рекомендуемая сложность (чем выше — тем медленнее, но безопаснее)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Проверяет, соответствует ли введённый пароль хэшу
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}