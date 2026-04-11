package main.Models.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_nutrients")
public class ProductNutrient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nutrient_id", nullable = false)
    private Nutrient nutrient;

    @Column(name = "amount")
    private Double amount;

    public ProductNutrient() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Nutrient getNutrient() { return nutrient; }
    public void setNutrient(Nutrient nutrient) { this.nutrient = nutrient; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}