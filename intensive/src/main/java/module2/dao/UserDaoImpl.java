package module2.dao;

import module2.config.HibernateSessionFactory;
import module2.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao{
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    public UserDaoImpl() {}

    @Override
    public void save(User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateSessionFactory.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.persist(user);
            transaction.commit();

            session.close();
            logger.info("Пользователь успешно добавлен: {}", user.getEmail());
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка: Пользователь с таким email {} уже существует", user.getEmail());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при добавлении пользователя: ", e);
        }   finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            User user = session.find(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from User", User.class).list();
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей: ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void update(User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateSessionFactory.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.merge(user);
            transaction.commit();

            session.close();
            logger.info("Данные пользователя {} обновлены: ID {}",user.getEmail(), user.getId());
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка обновления: email {} уже занят", user.getEmail());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при обновлении пользователя: ", e);
        } finally {
            if (session != null && session.isOpen()){
                session.close();
            }
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("Пользователь {} с ID {} удален.",user.getEmail(), id);
            } else {
                logger.warn("Пользователь с ID {} не найден для удаления.", id);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при удалении пользователя с ID: {}", id, e);
        }
    }
}
