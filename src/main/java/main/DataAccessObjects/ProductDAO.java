package main.DataAccessObjects;

import main.Models.Entities.*;
import main.Interface.DAO;
import main.Utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ProductDAO implements DAO<Product> {

    @Override
    public void save(Product product) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(product);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(Product product) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(product);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(Product product) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(product);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public Product findById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Product product = session.get(Product.class, id);
        session.close();
        return product;
    }

    @Override
    public List<Product> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Product> products = session.createQuery("FROM Product", Product.class).getResultList();
        session.close();
        return products;
    }

    // Поиск продуктов по названию
    public List<Product> search(String query) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Product> products = session.createQuery("FROM Product WHERE name LIKE :query", Product.class)
                .setParameter("query", "%" + query + "%")
                .getResultList();
        session.close();
        return products;
    }

    public void saveOrUpdateWithNutrients(Product product) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // === 1. Категория ===
            if (product.getCategory() != null) {
                Category category = findOrCreateCategory(session, product.getCategory().getName());
                product.setCategory(category);
            }

            // === 2. Если редактирование — удаляем старые нутриенты ===
            if (product.getId() != 0) {
                session.createQuery("DELETE FROM ProductNutrient WHERE product.id = :pid")
                        .setParameter("pid", product.getId())
                        .executeUpdate();
            }

            // === 3. Сохраняем/обновляем нутриенты ===
            if (product.getNutrients() != null) {
                for (ProductNutrient pn : product.getNutrients()) {
                    if (pn.getNutrient() != null && pn.getNutrient().getName() != null) {
                        Nutrient nutrient = findOrCreateNutrient(
                                session,
                                pn.getNutrient().getName(),
                                pn.getNutrient().getUnit(),
                                pn.getNutrient().getType()
                        );
                        pn.setNutrient(nutrient);
                        pn.setProduct(product);           // важная связь
                        session.save(pn);                 // сохраняем связь
                    }
                }
            }

            // === 4. Сохраняем/обновляем сам продукт ===
            session.saveOrUpdate(product);

            tx.commit();
            System.out.println("✅ Продукт " + (product.getId() == 0 ? "добавлен" : "обновлён") + ": " + product.getName());

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Ошибка сохранения продукта: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    private Category findOrCreateCategory(Session session, String name) {
        Category cat = session.createQuery("FROM Category WHERE name = :name", Category.class)
                .setParameter("name", name)
                .uniqueResult();

        if (cat == null) {
            cat = new Category();
            cat.setName(name);
            session.save(cat);
        }
        return cat;
    }

    private Nutrient findOrCreateNutrient(Session session, String name, String unit, String type) {
        Nutrient nut = session.createQuery("FROM Nutrient WHERE name = :name", Nutrient.class)
                .setParameter("name", name)
                .uniqueResult();

        if (nut == null) {
            nut = new Nutrient();
            nut.setName(name);
            nut.setUnit(unit);
            nut.setType(type);
            session.save(nut);
        }
        return nut;
    }
}