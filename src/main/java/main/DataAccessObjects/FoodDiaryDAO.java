package main.DataAccessObjects;

import main.Models.Entities.FoodDiary;
import main.Utility.HibernateUtil;
import main.Interface.DAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import main.Models.Entities.User;

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

    /**
     * Возвращает существующий дневник пользователя за дату или создаёт новый
     */
    public FoodDiary getOrCreateDiary(int userId, LocalDate date) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            // Ищем дневник за указанную дату
            FoodDiary diary = session.createQuery(
                            "FROM FoodDiary fd " +
                                    "WHERE fd.user.id = :userId AND fd.date = :date", FoodDiary.class)
                    .setParameter("userId", userId)
                    .setParameter("date", date)
                    .uniqueResult();

            if (diary != null) {
                tx.commit();
                return diary;
            }

            // Создаём новый дневник
            diary = new FoodDiary();
            diary.setUser(session.get(User.class, userId));   // загружаем пользователя
            diary.setDate(date);

            session.save(diary);
            tx.commit();

            System.out.println("✅ Создан новый дневник для пользователя " + userId + " на дату " + date);
            return diary;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            System.err.println("❌ Ошибка при получении/создании дневника: " + e.getMessage());
            return null;
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