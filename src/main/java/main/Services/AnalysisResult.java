package main.Services;

import main.Models.Entities.NutritionNorms;

import java.time.LocalDate;

public class AnalysisResult {
    private LocalDate date;
    private NutritionNorms norms;
    private double consumedCalories;
    private double consumedProteins;
    private double consumedFats;
    private double consumedCarbs;

    private double caloriesPercent;
    private double proteinsPercent;
    private double fatsPercent;
    private double carbsPercent;

    private String recommendations;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public NutritionNorms getNorms() { return norms; }
    public void setNorms(NutritionNorms norms) { this.norms = norms; }

    public double getConsumedCalories() { return consumedCalories; }
    public void setConsumedCalories(double consumedCalories) { this.consumedCalories = consumedCalories; }

    public double getConsumedProteins() { return consumedProteins; }
    public void setConsumedProteins(double consumedProteins) { this.consumedProteins = consumedProteins; }

    public double getConsumedFats() { return consumedFats; }
    public void setConsumedFats(double consumedFats) { this.consumedFats = consumedFats; }

    public double getConsumedCarbs() { return consumedCarbs; }
    public void setConsumedCarbs(double consumedCarbs) { this.consumedCarbs = consumedCarbs; }

    public double getCaloriesPercent() { return caloriesPercent; }
    public void setCaloriesPercent(double caloriesPercent) { this.caloriesPercent = caloriesPercent; }

    public double getProteinsPercent() { return proteinsPercent; }
    public void setProteinsPercent(double proteinsPercent) { this.proteinsPercent = proteinsPercent; }

    public double getFatsPercent() { return fatsPercent; }
    public void setFatsPercent(double fatsPercent) { this.fatsPercent = fatsPercent; }

    public double getCarbsPercent() { return carbsPercent; }
    public void setCarbsPercent(double carbsPercent) { this.carbsPercent = carbsPercent; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
}