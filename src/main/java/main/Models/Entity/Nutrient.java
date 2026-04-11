package main.Models.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "nutrients")
public class Nutrient implements Serializable {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;

    @Column(name = "type")
    private String type;        // "macro" или "micro"

    public Nutrient() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}