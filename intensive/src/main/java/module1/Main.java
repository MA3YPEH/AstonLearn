package module1;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        MyHashMap<String, String> map = new MyHashMap<>();

        map.put("Россия", "Москва");
        map.put("Франция", "Париж");
        map.put("Япония", "Токио");

        System.out.println(map);

        map.remove("Франция");

        System.out.println("-------------------------------");

        for (Map.Entry<String, String> entry : map) {
            System.out.println("Страна: " + entry.getKey() + " | Столица: " + entry.getValue());
        }
    }
}