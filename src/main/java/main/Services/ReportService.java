package main.Services;

import main.Models.Entities.FoodEntry;
import main.Models.Entities.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {

    private final FoodDiaryService diaryService = new FoodDiaryService();
    private final AnalysisService analysisService = new AnalysisService();

    /**
     * Генерация отчёта за один день и сохранение в текстовый файл
     */
    public String generateDailyReport(int userId, LocalDate date) {
        List<FoodEntry> entries = diaryService.getEntriesForDate(userId, date);
        AnalysisResult analysis = analysisService.analyzeDay(userId, date);

        StringBuilder report = new StringBuilder();
        report.append("====================================\n");
        report.append("     ОТЧЁТ ПО ПИТАНИЮ ЗА ").append(date).append("\n");
        report.append("====================================\n\n");

        report.append("Пользователь: ").append(analysis.getNorms().getProfile().getUser().getLogin()).append("\n");
        report.append("Дата: ").append(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append("\n\n");

        report.append("=== НОРМЫ КБЖУ ===\n");
        report.append(String.format("Калории:     %.0f ккал\n", analysis.getNorms().getCalories()));
        report.append(String.format("Белки:       %.0f г\n", analysis.getNorms().getProteins()));
        report.append(String.format("Жиры:        %.0f г\n", analysis.getNorms().getFats()));
        report.append(String.format("Углеводы:    %.0f г\n\n", analysis.getNorms().getCarbs()));

        report.append("=== ФАКТИЧЕСКИ СЪЕДЕНО ===\n");
        report.append(String.format("Калории:     %.0f ккал (%.1f%%)\n",
                analysis.getConsumedCalories(), analysis.getCaloriesPercent()));
        report.append(String.format("Белки:       %.0f г (%.1f%%)\n",
                analysis.getConsumedProteins(), analysis.getProteinsPercent()));
        report.append(String.format("Жиры:        %.0f г (%.1f%%)\n",
                analysis.getConsumedFats(), analysis.getFatsPercent()));
        report.append(String.format("Углеводы:    %.0f г (%.1f%%)\n\n",
                analysis.getConsumedCarbs(), analysis.getCarbsPercent()));

        report.append("=== РЕКОМЕНДАЦИИ ===\n");
        report.append(analysis.getRecommendations());

        report.append("\n\n====================================\n");
        report.append("Отчёт сгенерирован: ").append(LocalDate.now());

        return report.toString();
    }

    /**
     * Сохранить отчёт в текстовый файл
     */
    public String saveReportToFile(int userId, LocalDate date) {
        String reportText = generateDailyReport(userId, date);
        String fileName = "Report_" + userId + "_" + date + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(reportText);
            System.out.println("✅ Отчёт сохранён в файл: " + fileName);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при сохранении отчёта в файл");
        }
    }
}