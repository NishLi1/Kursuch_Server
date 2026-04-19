package main.Services;

import main.DataAccessObjects.UserProfileDAO;
import main.Models.Entities.User;
import main.Models.Entities.UserProfile;
import main.Models.Entities.NutritionNorms;
import main.Services.NutritionService;

public class UserProfileService {

    private final UserProfileDAO profileDAO = new UserProfileDAO();
    private final NutritionService nutritionService = new NutritionService();

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
    public UserProfile updateProfile(UserProfile incomingProfile) {
        // Находим существующий профиль
        UserProfile existingProfile = profileDAO.findByUserId(incomingProfile.getUser().getId());

        if (existingProfile == null) {
            throw new RuntimeException("Профиль пользователя не найден");
        }

        // Обновляем данные профиля
        existingProfile.setDateOfBirth(incomingProfile.getDateOfBirth());
        existingProfile.setGender(incomingProfile.getGender());
        existingProfile.setWeight(incomingProfile.getWeight());
        existingProfile.setHeight(incomingProfile.getHeight());
        existingProfile.setActivityLevel(incomingProfile.getActivityLevel());

        profileDAO.update(existingProfile);

        // === ОБНОВЛЯЕМ ИЛИ СОЗДАЁМ НОРМЫ (одна запись на профиль) ===
        NutritionNorms norms = existingProfile.getNutritionNorms();
        if (norms == null) {
            norms = new NutritionNorms();
            norms.setProfile(existingProfile);   // важная связь
            existingProfile.setNutritionNorms(norms);
        }

        // Пересчитываем нормы
        NutritionNorms calculated = nutritionService.calculateNorms(existingProfile);
        norms.setCalories(calculated.getCalories());
        norms.setProteins(calculated.getProteins());
        norms.setFats(calculated.getFats());
        norms.setCarbs(calculated.getCarbs());

        profileDAO.update(existingProfile);   // сохраняем каскадом

        return existingProfile;
    }

    /**
     * Получить полный профиль пользователя вместе с рассчитанными нормами КБЖУ
     */
    public UserProfile getFullProfileByUserId(int userId) {
        UserProfile profile = profileDAO.findByUserId(userId);

        if (profile != null && profile.getNutritionNorms() == null) {
            // Если норм ещё нет — рассчитываем и сохраняем
            NutritionNorms norms = nutritionService.calculateNorms(profile);
            profile.setNutritionNorms(norms);
            profileDAO.update(profile);   // сохраняем нормы
        }

        return profile;
    }
}