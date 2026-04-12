package main.Utility;

import main.Models.Entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static final String CONFIG_FILE = "hibernate.cfg.xml";

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure(CONFIG_FILE);

                configuration.addAnnotatedClass(Role.class);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(UserProfile.class);
                configuration.addAnnotatedClass(NutritionNorms.class);
                configuration.addAnnotatedClass(Category.class);
                configuration.addAnnotatedClass(Product.class);
                configuration.addAnnotatedClass(Nutrient.class);
                configuration.addAnnotatedClass(ProductNutrient.class);
                configuration.addAnnotatedClass(FoodDiary.class);
                configuration.addAnnotatedClass(FoodEntry.class);

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure(CONFIG_FILE);

                sessionFactory = configuration.buildSessionFactory(builder.build());

                System.out.println("✅ Hibernate SessionFactory успешно создан!");

            } catch (Exception e) {
                System.err.println("❌ Ошибка при создании SessionFactory!");
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}