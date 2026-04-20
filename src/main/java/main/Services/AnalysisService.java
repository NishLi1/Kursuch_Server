package main.Services;

import main.Models.Entities.*;
import main.Models.AnalysisItem;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class AnalysisService {

    private final FoodDiaryService diaryService = new FoodDiaryService();
    private final NutritionService nutritionService = new NutritionService();

    /**
     * Основной метод анализа рациона за день
     */
    public AnalysisResult analyzeDay(int userId, LocalDate date) {
        FoodDiary diary = diaryService.getOrCreateDiary(userId, date);
        List<FoodEntry> entries = diaryService.getEntriesForDate(userId, date);

        UserProfile profile = diary.getUser().getUserProfile();
        NutritionNorms norms = nutritionService.calculateNorms(profile);

        double totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;

        for (FoodEntry entry : entries) {
            if (entry.getProduct() == null || entry.getWeight() == null || entry.getWeight() <= 0) {
                continue;
            }

            Product p = entry.getProduct();
            double factor = entry.getWeight() / 100.0;

            totalCalories += p.getCalories() * factor;
            totalProteins += p.getProteins() * factor;
            totalFats += p.getFats() * factor;
            totalCarbs += p.getCarbs() * factor;
        }

        AnalysisResult result = new AnalysisResult();
        result.setDate(date);
        result.setNorms(norms);
        result.setConsumedCalories(totalCalories);
        result.setConsumedProteins(totalProteins);
        result.setConsumedFats(totalFats);
        result.setConsumedCarbs(totalCarbs);

        result.setCaloriesPercent(calculatePercent(totalCalories, norms.getCalories()));
        result.setProteinsPercent(calculatePercent(totalProteins, norms.getProteins()));
        result.setFatsPercent(calculatePercent(totalFats, norms.getFats()));
        result.setCarbsPercent(calculatePercent(totalCarbs, norms.getCarbs()));

        result.setRecommendations(generateRecommendations(result));

        return result;
    }

    private double calculatePercent(double consumed, double norm) {
        if (norm == 0) return 0;
        return Math.min(100.0, (consumed / norm) * 100);
    }

    private String generateRecommendations(AnalysisResult result) {
        StringBuilder sb = new StringBuilder();

        if (result.getCaloriesPercent() > 110) {
            sb.append("• Превышение калорийности. Рекомендуется уменьшить порции.\n");
        } else if (result.getCaloriesPercent() < 70) {
            sb.append("• Недостаток калорий. Добавьте больше продуктов.\n");
        }

        if (result.getProteinsPercent() < 80) {
            sb.append("• Мало белка. Добавьте мясо, рыбу, творог или бобовые.\n");
        }
        if (result.getFatsPercent() < 70) {
            sb.append("• Мало жиров. Добавьте орехи, авокадо или растительные масла.\n");
        }
        if (result.getCarbsPercent() > 120) {
            sb.append("• Слишком много углеводов. Сократите сладкое и мучное.\n");
        }

        if (sb.length() == 0) {
            sb.append("• Рацион сбалансирован. Отличная работа!");
        }

        return sb.toString();
    }

    public List<AnalysisItem> getDayAnalysisAsItems(int userId, LocalDate date) {
        AnalysisResult result = analyzeDay(userId, date);
        List<AnalysisItem> items = new ArrayList<>();

        items.add(new AnalysisItem("Калории",
                Math.round(result.getConsumedCalories()) + " ккал",
                Math.round(result.getNorms().getCalories()) + " ккал",
                Math.round(result.getCaloriesPercent()) + "%",
                getStatus(result.getCaloriesPercent())));

        items.add(new AnalysisItem("Белки",
                Math.round(result.getConsumedProteins()) + " г",
                Math.round(result.getNorms().getProteins()) + " г",
                Math.round(result.getProteinsPercent()) + "%",
                getStatus(result.getProteinsPercent())));

        items.add(new AnalysisItem("Жиры",
                Math.round(result.getConsumedFats()) + " г",
                Math.round(result.getNorms().getFats()) + " г",
                Math.round(result.getFatsPercent()) + "%",
                getStatus(result.getFatsPercent())));

        items.add(new AnalysisItem("Углеводы",
                Math.round(result.getConsumedCarbs()) + " г",
                Math.round(result.getNorms().getCarbs()) + " г",
                Math.round(result.getCarbsPercent()) + "%",
                getStatus(result.getCarbsPercent())));

        return items;
    }

    private String getStatus(double percent) {
        if (percent < 70) return "Недостаток";
        if (percent > 130) return "Избыток";
        return "Норма";
    }
}