package module2.ui;

import module2.config.HibernateSessionFactory;
import module2.dao.UserDao;
import module2.dao.UserDaoImpl;
import module2.models.User;

import java.util.List;
import java.util.Scanner;

public class ConsoleInterface {
    private final Scanner in = new Scanner(System.in);
    private UserDao userDao;

    public void start(){
        if (!connectToDatabase()) {
            System.out.println("Завершение работы программы.");
            return;
        }

        this.userDao = new UserDaoImpl();
        boolean exit = false;

        while (!exit){
            printMenu();
            String choice = in.nextLine().trim();

            try{
                switch (choice){
                    case "1" -> createUser();
                    case "2" -> readUser();
                    case "3" -> readAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "6" -> {
                        exit = true;
                        System.out.println("Выход из приложения");
                    }
                    default -> System.out.println("Неверная команда. Попробуйте снова.");
                }
            }catch (Exception e){
                System.out.println("Ошибонька: " + e.getMessage());
            }
        }
    }

    private void createUser() {
        System.out.println("Введите имя пользователя: ");
        String name = in.nextLine();

        System.out.println("Введите email пользователя: ");
        String email = in.nextLine();

        System.out.println("Введите возраст пользователя: ");
        int age = Integer.parseInt(in.nextLine());

        User user = new User(name, email, age);
        userDao.save(user);
    }

    private void readUser() {
        System.out.println("Введите ID пользователя: ");
        Long id = Long.parseLong(in.nextLine());

        userDao.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Пользователь с таким ID не найден.")
        );
    }

    private void readAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private void updateUser() {
        System.out.print("Введите ID пользователя который нужно обновить: ");
        Long id = Long.parseLong(in.nextLine());

        userDao.findById(id).ifPresentOrElse(user -> {
            System.out.print("Введите новое имя (оставьте пустым для прежнего значения): ");
            String name = in.nextLine();

            if (!name.isBlank()) user.setName(name);

            System.out.print("Введите новый email (оставьте пустым для прежнего значения): ");
            String email = in.nextLine();

            if (!email.isBlank()) user.setEmail(email);

            System.out.print("Введите новый возраст (оставьте пустым для прежнего значения): ");
            String age = in.nextLine();

            if (!age.isBlank()) user.setAge(Integer.parseInt(age));

            userDao.update(user);
        }, () -> System.out.println("Пользователь с таким ID не найден."));
    }

    private void deleteUser() {
        System.out.println("Введите ID пользователя для удаления: ");
        Long id = Long.parseLong(in.nextLine());
        userDao.delete(id);
    }

    private void printMenu() {
        System.out.println("\n=== USER MENU ===");
        System.out.println("1. Создать пользователя (Create)");
        System.out.println("2. Найти пользователя по ID (Read)");
        System.out.println("3. Показать всех пользователей (Read All)");
        System.out.println("4. Обновить данные пользователя (Update)");
        System.out.println("5. Удалить пользователя (Delete)");
        System.out.println("6. Выйти");
        System.out.print("Выберите действие: ");
    }

    private boolean connectToDatabase() {
        System.out.println("ПОДКЛЮЧЕНИЕ К БД");

        System.out.println("Введите логин:");
        String username = in.nextLine().trim();

        System.out.print("Введите пароль: ");
        String password = in.nextLine();

        try {
            System.out.println("Попытка подключения к БД...");
            HibernateSessionFactory.initialize(username, password);
            System.out.println("Успешно подключено!\n");
            return true;
        }catch (Exception e){
            System.out.println("\nОшибка подключения: " + e.getMessage());
            return false;
        }
    }
}
