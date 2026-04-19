package main.Models.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "food_entries")
public class FoodEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private FoodDiary diary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "weight")
    private Double weight;

    @Transient
    private Double calories;
    @Transient
    private Double proteins;
    @Transient
    private Double fats;
    @Transient
    private Double carbs;

    public FoodEntry() {}

    // Конструктор для создания записи
    public FoodEntry(Product product, Double weight) {
        this.product = product;
        this.weight = weight;
    }

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

    @Override
    public String toString() {
        return "FoodEntry{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", weight=" + weight +
                '}';
    }
}