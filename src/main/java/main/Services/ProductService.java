package main.Services;

import main.DataAccessObjects.ProductDAO;
import main.Models.Entities.Product;
import main.Models.Entities.ProductNutrient;

import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Получить все продукты
     */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Поиск продуктов по названию
     */
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productDAO.findAll();
        }
        return productDAO.search(query.trim());
    }

    /**
     * Добавить новый продукт (для сотрудника / админа)
     */
    public void addProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Название продукта не может быть пустым");
        }
        productDAO.saveWithNutrients(product);
    }

    /**
     * Обновить существующий продукт
     */
    public void updateProduct(Product product) {
        if (product.getId() == 0) {
            throw new RuntimeException("Нельзя обновить продукт без ID");
        }
        productDAO.update(product);
    }

    /**
     * Удалить продукт (только для админа)
     */
    public void deleteProduct(int productId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Продукт не найден");
        }
        productDAO.delete(product);
    }

    /**
     * Получить продукт по ID
     */
    public Product getProductById(int id) {
        return productDAO.findById(id);
    }
}