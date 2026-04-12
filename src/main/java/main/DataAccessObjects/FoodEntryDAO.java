package main.DataAccessObjects;

import main.Models.Entities.FoodEntry;
import main.Utility.HibernateUtil;
import main.Interface.DAO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FoodEntryDAO implements DAO<FoodEntry> {

    @Override
    public void save(FoodEntry entry) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(entry);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(FoodEntry entry) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(entry);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(FoodEntry entry) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(entry);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public FoodEntry findById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        FoodEntry entry = session.get(FoodEntry.class, id);
        session.close();
        return entry;
    }

    @Override
    public List<FoodEntry> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<FoodEntry> entries = session.createQuery("FROM FoodEntry", FoodEntry.class).getResultList();
        session.close();
        return entries;
    }

    // Получить все записи в дневнике
    public List<FoodEntry> findByDiaryId(int diaryId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<FoodEntry> entries = session.createQuery(
                        "FROM FoodEntry WHERE diary.id = :diaryId", FoodEntry.class)
                .setParameter("diaryId", diaryId)
                .getResultList();
        session.close();
        return entries;
    }
}