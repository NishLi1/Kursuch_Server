package main.DataAccessObjects;

import main.Models.Entities.FoodDiary;
import main.Utility.HibernateUtil;
import main.Interface.DAO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class FoodDiaryDAO implements DAO<FoodDiary> {

    @Override
    public void save(FoodDiary diary) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(diary);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(FoodDiary diary) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(diary);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(FoodDiary diary) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(diary);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public FoodDiary findById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        FoodDiary diary = session.get(FoodDiary.class, id);
        session.close();
        return diary;
    }

    @Override
    public List<FoodDiary> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<FoodDiary> diaries = session.createQuery("FROM FoodDiary", FoodDiary.class).getResultList();
        session.close();
        return diaries;
    }

    // Важный метод — получить дневник пользователя за дату
    public FoodDiary findByUserAndDate(int userId, LocalDate date) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        FoodDiary diary = session.createQuery(
                        "FROM FoodDiary WHERE user.id = :userId AND date = :date", FoodDiary.class)
                .setParameter("userId", userId)
                .setParameter("date", date)
                .uniqueResult();
        session.close();
        return diary;
    }
}