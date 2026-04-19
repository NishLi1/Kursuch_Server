package main.Models;

import java.io.Serializable;

public class AnalysisItem implements Serializable {

    private String nutrient;
    private String consumed;
    private String norm;
    private String percent;
    private String status;   // "Норма", "Недостаток", "Избыток"

    public AnalysisItem() {}

    public AnalysisItem(String nutrient, String consumed, String norm, String percent, String status) {
        this.nutrient = nutrient;
        this.consumed = consumed;
        this.norm = norm;
        this.percent = percent;
        this.status = status;
    }

    public String getNutrient() { return nutrient; }
    public void setNutrient(String nutrient) { this.nutrient = nutrient; }

    public String getConsumed() { return consumed; }
    public void setConsumed(String consumed) { this.consumed = consumed; }

    public String getNorm() { return norm; }
    public void setNorm(String norm) { this.norm = norm; }

    public String getPercent() { return percent; }
    public void setPercent(String percent) { this.percent = percent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}