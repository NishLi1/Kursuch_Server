package main.Services;

import main.Models.Entities.NutritionNorms;
import main.Models.Entities.UserProfile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NutritionService {

    /**
     * Основной метод: рассчитывает суточные нормы КБЖУ
     */
    public NutritionNorms calculateNorms(UserProfile profile) {
        if (profile == null) {
            throw new RuntimeException("Профиль пользователя не заполнен");
        }

        double age = calculateAge(profile.getDateOfBirth());
        double weight = profile.getWeight();
        double height = profile.getHeight();
        double activityLevel = profile.getActivityLevel();

        double bmr; // Basal Metabolic Rate (БОВ)

        if (profile.getGender() == null) {
            throw new RuntimeException("Не указан пол");
        }

        if ("male".equalsIgnoreCase(profile.getGender()) || "мужской".equalsIgnoreCase(profile.getGender())) {
            // Формула для мужчин
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            // Формула для женщин
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        // Итоговая калорийность с учётом активности
        double calories = bmr * activityLevel;

        // Расчёт БЖУ
        double proteins = weight * 1.8;           // 1.8 г/кг (среднее для активных)
        double fats = weight * 1.0;               // 1.0 г/кг
        double carbs = (calories - (proteins * 4 + fats * 9)) / 4; // остаток на углеводы

        NutritionNorms norms = new NutritionNorms();
        norms.setProfile(profile);
        norms.setCalories((double) Math.round(calories));
        norms.setProteins((double) Math.round(proteins));
        norms.setFats((double) Math.round(fats));
        norms.setCarbs((double) Math.round(carbs));

        return norms;
    }

    /**
     * Вспомогательный метод: расчёт возраста
     */
    private double calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 30; // значение по умолчанию
        return ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }
}