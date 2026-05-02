package main.DataAccessObjects;

import main.Models.Entities.*;
import main.Interface.DAO;
import main.Utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.ArrayList;

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

            // 1. Разрешаем категорию (по имени)
            if (product.getCategory() != null && product.getCategory().getName() != null) {
                Category cat = session.createQuery(
                                "FROM Category c WHERE c.name = :name", Category.class)
                        .setParameter("name", product.getCategory().getName().trim())
                        .uniqueResult();

                if (cat == null) {
                    cat = new Category();
                    cat.setName(product.getCategory().getName().trim());
                    session.save(cat);
                }
                product.setCategory(cat);
            }

            // 2. Если редактируем — удаляем старые связи нутриентов
            if (product.getId() != 0) {
                session.createQuery("DELETE FROM ProductNutrient pn WHERE pn.product.id = :pid")
                        .setParameter("pid", product.getId())
                        .executeUpdate();
            }

            // 3. Разрешаем/создаём нутриенты и создаём чистые ProductNutrient
            List<ProductNutrient> resolvedNutrients = new ArrayList<>();
            if (product.getNutrients() != null) {
                for (ProductNutrient pn : product.getNutrients()) {
                    if (pn.getNutrient() == null || pn.getNutrient().getName() == null) continue;

                    Nutrient nut = session.createQuery(
                                    "FROM Nutrient n WHERE n.name = :name", Nutrient.class)
                            .setParameter("name", pn.getNutrient().getName().trim())
                            .uniqueResult();

                    if (nut == null) {
                        nut = new Nutrient();
                        nut.setName(pn.getNutrient().getName().trim());
                        nut.setUnit(pn.getNutrient().getUnit());
                        nut.setType(pn.getNutrient().getType());
                        session.save(nut);
                    }

                    // Создаём чистую связь
                    ProductNutrient newPn = new ProductNutrient();
                    newPn.setNutrient(nut);
                    newPn.setProduct(product);
                    newPn.setAmount(pn.getAmount());
                    resolvedNutrients.add(newPn);
                }
            }

            // 4. Привязываем resolved нутриенты к продукту
            product.getNutrients().clear();           // очищаем старый список
            product.getNutrients().addAll(resolvedNutrients);

            // 5. Сохраняем/обновляем продукт (каскадно сохранит ProductNutrient)
            if (product.getId() == 0) {
                session.save(product);
            } else {
                session.update(product);
            }

            tx.commit();
            System.out.println("✅ Продукт успешно сохранён: " + product.getName());

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Ошибка сохранения продукта: " + e.getMessage(), e);
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

            session.save(nut);      // сохраняем
            session.flush();        // принудительно фиксируем в БД
            session.refresh(nut);   // обновляем состояние объекта
            System.out.println("✅ Создан новый Nutrient: " + name);
        }
        return nut;
    }

}