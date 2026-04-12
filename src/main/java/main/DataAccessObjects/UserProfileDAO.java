package main.DataAccessObjects;

import main.Models.Entities.UserProfile;
import main.Utility.HibernateUtil;
import main.Interface.DAO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserProfileDAO implements DAO<UserProfile> {

    @Override
    public void save(UserProfile profile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(profile);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(UserProfile profile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(profile);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(UserProfile profile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(profile);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public UserProfile findById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProfile profile = session.get(UserProfile.class, id);
        session.close();
        return profile;
    }

    @Override
    public List<UserProfile> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<UserProfile> profiles = session.createQuery("FROM UserProfile", UserProfile.class).getResultList();
        session.close();
        return profiles;
    }

    // Получить профиль по ID пользователя
    public UserProfile findByUserId(int userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProfile profile = session.createQuery(
                        "FROM UserProfile WHERE user.id = :userId", UserProfile.class)
                .setParameter("userId", userId)
                .uniqueResult();
        session.close();
        return profile;
    }
}