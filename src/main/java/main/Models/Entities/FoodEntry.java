package main.Models.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "food_entries")
public class FoodEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false)
    private FoodDiary diary;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "weight")
    private Double weight;

    // Вычисляемые поля (заполняются на сервере при запросе дневника)
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbs;

    public FoodEntry() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public FoodDiary getDiary() { return diary; }
    public void setDiary(FoodDiary diary) { this.diary = diary; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProteins() { return proteins; }
    public void setProteins(Double proteins) { this.proteins = proteins; }

    public Double getFats() { return fats; }
    public void setFats(Double fats) { this.fats = fats; }

    public Double getCarbs() { return carbs; }
    public void setCarbs(Double carbs) { this.carbs = carbs; }
}