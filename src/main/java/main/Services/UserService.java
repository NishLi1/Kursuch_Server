package main.Services;

import main.DataAccessObjects.UserDAO;
import main.DataAccessObjects.UserProfileDAO;
import main.Models.Entities.Role;
import main.Models.Entities.User;
import main.Models.Entities.UserProfile;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final UserProfileDAO profileDAO = new UserProfileDAO();

    /**
     * Регистрация нового пользователя
     */
    public User register(User user, UserProfile profile) {
        try {
            // Проверяем, существует ли уже такой логин
            if (userDAO.findByLogin(user.getLogin()) != null) {
                throw new RuntimeException("Пользователь с таким логином уже существует");
            }

            // Создаём роль по умолчанию (USER)
            Role defaultRole = new Role();
            defaultRole.setName("USER");

            user.setRole(defaultRole);

            // Сохраняем пользователя
            userDAO.save(user);

            // Привязываем профиль к пользователю
            profile.setUser(user);
            profileDAO.save(profile);

            System.out.println("✅ Пользователь " + user.getLogin() + " успешно зарегистрирован");
            return user;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при регистрации: " + e.getMessage());
        }
    }

    /**
     * Авторизация пользователя
     */
    public User login(String login, String passwordHash) {
        User user = userDAO.findByLoginAndPasswordHash(login, passwordHash);
        if (user == null) {
            throw new RuntimeException("Неверный логин или пароль");
        }
        return user;
    }

    /**
     * Получить пользователя по ID
     */
    public User getUserById(int id) {
        return userDAO.findById(id);
    }

    /**
     * Получить всех пользователей (для админа)
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Изменить роль пользователя
     */
    public void changeUserRole(int userId, String newRoleName) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Role newRole = new Role();
        newRole.setName(newRoleName.toUpperCase());

        user.setRole(newRole);
        userDAO.update(user);

        System.out.println("✅ Роль пользователя " + user.getLogin() + " изменена на " + newRoleName);
    }

    /**
     * Обновить профиль пользователя
     */
    public void updateProfile(UserProfile profile) {
        profileDAO.update(profile);
    }

    /**
     * Получить профиль пользователя
     */
    public UserProfile getProfileByUserId(int userId) {
        return profileDAO.findByUserId(userId);
    }
}