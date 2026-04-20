package main.Services;

import main.DataAccessObjects.UserDAO;
import main.DataAccessObjects.UserProfileDAO;
import main.Models.Entities.Role;
import main.Models.Entities.User;
import main.Models.Entities.UserProfile;
import main.Utility.*;
import org.hibernate.Session;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final UserProfileDAO profileDAO = new UserProfileDAO();

    /**
     * Регистрация нового пользователя
     */
    public User register(User user, UserProfile profile) {
        try {
            // 1. Проверка логина
            if (userDAO.findByLogin(user.getLogin()) != null) {
                throw new RuntimeException("Пользователь с таким логином уже существует");
            }

            // 2. Устанавливаем роль (ищем существующую или создаём новую)
            Role defaultRole = getOrCreateDefaultRole();
            user.setRole(defaultRole);

            // 3. Устанавливаем двустороннюю связь ДО сохранения
            profile.setUser(user);
            user.setUserProfile(profile);

            // Хэшируем пароль перед сохранением
            user.setPasswordHash(PasswordUtils.hashPassword(user.getPasswordHash()));

            // 4. Сохраняем User (UserProfile сохранится каскадом благодаря CascadeType.ALL)
            userDAO.save(user);

            System.out.println("✅ Пользователь " + user.getLogin() + " успешно зарегистрирован");
            return user;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при регистрации: " + e.getMessage());
        }
    }

    /**
     * Получить роль по умолчанию или создать её, если не существует
     */
    private Role getOrCreateDefaultRole() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // Пытаемся найти роль USER
            Role role = session.createQuery("FROM Role WHERE name = :name", Role.class)
                    .setParameter("name", "USER")
                    .uniqueResult();
            
            if (role != null) {
                return role;
            }
            
            // Если не нашли — создаём новую с ID = 1
            role = new Role();
            role.setId(1);
            role.setName("USER");
            session.beginTransaction();
            session.save(role);
            session.getTransaction().commit();
            
            return role;
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            // Если и возникла ошибка, возвращаем роль с ID = 1 (надеемся, что она есть)
            Role fallbackRole = new Role();
            fallbackRole.setId(1);
            fallbackRole.setName("USER");
            return fallbackRole;
        } finally {
            session.close();
        }
    }


    /**
     * Авторизация пользователя
     */
    public User login(String login, String plainPassword) {
        User user = userDAO.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Неверный логин или пароль");
        }

        // Проверяем хэш
        if (!PasswordUtils.checkPassword(plainPassword, user.getPasswordHash())) {
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

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // Ищем существующую роль
            Role newRole = session.createQuery("FROM Role WHERE name = :name", Role.class)
                    .setParameter("name", newRoleName.toUpperCase())
                    .uniqueResult();
            
            if (newRole == null) {
                throw new RuntimeException("Роль '" + newRoleName + "' не найдена в базе данных");
            }
            
            user.setRole(newRole);
            userDAO.update(user);
            
            System.out.println("✅ Роль пользователя " + user.getLogin() + " изменена на " + newRoleName);
        } finally {
            session.close();
        }
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