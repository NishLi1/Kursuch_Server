package main.DataAccessObjects;

import main.Models.Entities.NutritionNorms;
import main.Utility.HibernateUtil;
import main.Interface.DAO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class NutritionNormsDAO implements DAO<NutritionNorms> {

    @Override
    public void save(NutritionNorms norms) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(norms);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(NutritionNorms norms) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(norms);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(NutritionNorms norms) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(norms);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public NutritionNorms findById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        NutritionNorms norms = session.get(NutritionNorms.class, id);
        session.close();
        return norms;
    }

    @Override
    public List<NutritionNorms> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<NutritionNorms> normsList = session.createQuery("FROM NutritionNorms", NutritionNorms.class).getResultList();
        session.close();
        return normsList;
    }

    public NutritionNorms findByProfileId(int profileId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        NutritionNorms norms = session.createQuery(
                        "FROM NutritionNorms WHERE profile.id = :profileId", NutritionNorms.class)
                .setParameter("profileId", profileId)
                .uniqueResult();
        session.close();
        return norms;
    }
}