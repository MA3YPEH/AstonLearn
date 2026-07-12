package module2;

import module2.config.HibernateSessionFactory;
import module2.ui.ConsoleInterface;

public class Main {
    public static void main(String[] args) {
        try {
            ConsoleInterface ui = new ConsoleInterface();
            ui.start();
        } finally {
            HibernateSessionFactory.shutdown();
        }
    }
}
