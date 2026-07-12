package module2.dao;

import module2.config.HibernateSessionFactory;
import module2.models.User;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoImplTest {
    private UserDao userDao;

    @BeforeAll
    void initAll() {
        HibernateSessionFactory.initialize("sa", "");

        this.userDao = new UserDaoImpl();
    }

    @BeforeEach
    void setUp() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDownAll() {
        HibernateSessionFactory.shutdown();
    }

    @Test
    @DisplayName("Успешное сохранение и поиск пользователя")
    void testSaveAndFindUser() {
        User user = new User("Egor", "egor@mail.ru", 29);
        userDao.save(user);

        assertNotNull(user.getId());

        Optional<User> foundUserOpt = userDao.findById(user.getId());
        assertTrue(foundUserOpt.isPresent());

        User foundUser = foundUserOpt.get();
        assertEquals("Egor", foundUser.getName());
        assertEquals("egor@mail.ru", foundUser.getEmail());
        assertEquals(29, foundUser.getAge());
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя должен возвращать Optional.empty()")
    void testFindByIdNotFound() {
        Optional<User> foundUser = userDao.findById(999L);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Перехват ошибки уникальности при дублировании email")
    void testSaveDuplicateEmailException() {
        User user1 = new User("User1", "same@example.com", 20);
        User user2 = new User("User2", "same@example.com", 30);

        userDao.save(user1);

        userDao.save(user2);

        List<User> allUsers = userDao.findAll();
        assertEquals(1, allUsers.size(), "В базе должен остаться только один пользователь из-за дубликата email");
    }
}
