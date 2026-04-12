package main.Services;

import main.DataAccessObjects.UserProfileDAO;
import main.Models.Entities.User;
import main.Models.Entities.UserProfile;

public class UserProfileService {

    private final UserProfileDAO profileDAO = new UserProfileDAO();

    /**
     * Создать или обновить физиологический профиль пользователя
     */
    public UserProfile saveOrUpdateProfile(User user, UserProfile profile) {
        if (profile.getUser() == null) {
            profile.setUser(user);
        }

        // Если профиль уже существует — обновляем
        UserProfile existing = profileDAO.findByUserId(user.getId());
        if (existing != null) {
            profile.setId(existing.getId());   // сохраняем ID
            profileDAO.update(profile);
        } else {
            profileDAO.save(profile);
        }

        return profile;
    }

    /**
     * Получить профиль пользователя
     */
    public UserProfile getProfileByUserId(int userId) {
        return profileDAO.findByUserId(userId);
    }

    /**
     * Обновить профиль
     */
    public void updateProfile(UserProfile profile) {
        profileDAO.update(profile);
    }
}