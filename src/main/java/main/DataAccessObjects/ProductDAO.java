package main.DataAccessObjects;

import main.Models.Entities.Product;
import main.Models.Entities.ProductNutrient;
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

    // Сохранение продукта вместе с его нутриентами
    public void saveWithNutrients(Product product) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(product);

            for (ProductNutrient pn : product.getNutrients()) {
                pn.setProduct(product);           // важная связь
                session.save(pn);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}