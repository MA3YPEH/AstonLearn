package module2.config;

import module2.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateSessionFactory {
    private static final Logger logger = LogManager.getLogger(HibernateSessionFactory.class);
    private static SessionFactory sessionFactory;

    private HibernateSessionFactory(){}

    public static void initialize(String username, String password) {
        if (sessionFactory != null) {
            throw new IllegalStateException("Вы уже авторизованы");
        }
        try {
            Configuration configuration = new Configuration().configure();

            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);

            configuration.addAnnotatedClass(User.class);

            configuration.setProperty("hibernate.hbm2ddl.auto", "update");

            //configuration.setProperty("hibernate.connection.pool_size", "5");
            //configuration.setProperty("hibernate.connection.autocommit", "false");
            //configuration.setProperty("hibernate.connection.release_mode", "after_transaction");

            sessionFactory = configuration.buildSessionFactory();
            logger.info("Авторизация успешна");
        } catch (Exception e) {
            logger.error("Не удалось подключиться к БД: ", e);
            throw new RuntimeException("Ошибка авторизации. Проверьте логин и пароль.");
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("Вы не авторизовались!");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Сессия успешно закрыта.");
        }
    }
}
