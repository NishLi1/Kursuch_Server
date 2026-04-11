package main.Models.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "nutrition_norms")
public class NutritionNorms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile profile;

    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbs;

    public NutritionNorms() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProteins() { return proteins; }
    public void setProteins(Double proteins) { this.proteins = proteins; }

    public Double getFats() { return fats; }
    public void setFats(Double fats) { this.fats = fats; }

    public Double getCarbs() { return carbs; }
    public void setCarbs(Double carbs) { this.carbs = carbs; }
}