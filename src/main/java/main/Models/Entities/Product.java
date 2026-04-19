package main.Models.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductNutrient> nutrients = new ArrayList<>();

    public Product() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<ProductNutrient> getNutrients() { return nutrients; }
    public void setNutrients(List<ProductNutrient> nutrients) { this.nutrients = nutrients; }

    // ==================== ГЕТТЕРЫ КБЖУ ====================

    public Double getCalories() {
        return getNutrientAmount("Калории");
    }

    public Double getProteins() {
        return getNutrientAmount("Белки");
    }

    public Double getFats() {
        return getNutrientAmount("Жиры");
    }

    public Double getCarbs() {
        return getNutrientAmount("Углеводы");
    }

    private Double getNutrientAmount(String nutrientName) {
        if (nutrients == null || nutrients.isEmpty()) {
            System.out.println("DEBUG: Нет нутриентов у продукта " + name);
            return 0.0;
        }

        for (ProductNutrient pn : nutrients) {
            if (pn.getNutrient() != null && pn.getNutrient().getName().equalsIgnoreCase(nutrientName)) {
                Double amount = pn.getAmount();
                return amount != null ? amount : 0.0;
            }
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "'}";
    }
}