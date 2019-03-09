package CDiesel72;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Diesel on 09.03.2019.
 */
public class ControlRestMenu {

    public ControlRestMenu() {
    }

    private String inString(Scanner sc) {
        String st = null;
        while ((st == null) || (st.isEmpty())) {
            st = sc.nextLine();
        }
        return st;
    }

    private int inInt(Scanner sc) {
        int i = 0;
        boolean flag = true;
        while (flag) {
            try {
                String st = inString(sc);
                i = Integer.valueOf(st);
                flag = false;
            } catch (Exception ex) {
            }
        }

        return i;
    }

    private double inDouble(Scanner sc) {
        double d = 0;
        boolean flag = true;
        while (flag) {
            try {
                String st = inString(sc);
                d = Double.valueOf(st);
                flag = false;
            } catch (Exception ex) {
            }
        }

        return d;
    }

    private boolean inBool(Scanner sc) {
        boolean bl = false;
        boolean flag = true;
        while (flag) {
            String st = inString(sc);
            if ("y".equalsIgnoreCase(st)) {
                bl = true;
                flag = false;
            } else if ("n".equalsIgnoreCase(st)) {
                bl = false;
                flag = false;
            }
        }

        return bl;
    }

    private Dish newDish(Scanner sc) {
        System.out.println("Введите данные о блюде:");
        System.out.print("название: ");
        String name = inString(sc);
        System.out.print("цена: ");
        double price = inDouble(sc);
        System.out.print("вес: ");
        double weight = inDouble(sc);
        System.out.print("наличие скидки (Y/N): ");
        boolean discount = inBool(sc);
        return new Dish(name, price, weight, discount);
    }

    public void addDish(Scanner sc, EntityManager em) {
        try {
            Dish dish = newDish(sc);
            em.getTransaction().begin();
            em.persist(dish);
            em.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("ERROR: Ошибка записи в базу данных");
            em.getTransaction().rollback();
        }
    }

    public void getMenu(EntityManager em) {
        Query query = em.createQuery("SELECT d FROM Dish d", Dish.class);
        List<Dish> dishes = (List<Dish>) query.getResultList();
        toScreen("Меню:", dishes);
    }

    public void getMenuPrice(Scanner sc, EntityManager em) {
        System.out.println("Введите минимальную и максимальную цены блюда:");
        System.out.print("минимальная: ");
        double min = inDouble(sc);
        System.out.print("максимальная: ");
        double max = inDouble(sc);

        Query query = em.createQuery("SELECT d FROM Dish d WHERE d.price BETWEEN :min AND :max", Dish.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        List<Dish> dishes = (List<Dish>) query.getResultList();
        toScreen("Меню - блюда по цене от " + min + " до " + max + ":", dishes);
    }

    public void getMenuDisc(EntityManager em) {
        Query query = em.createQuery("SELECT d FROM Dish d WHERE d.discount = :bool", Dish.class);
        query.setParameter("bool", true);
        List<Dish> dishes = (List<Dish>) query.getResultList();
        toScreen("Меню - блюда со скидкой:", dishes);
    }

    public void getMenuWeight(Scanner sc, EntityManager em) {
        List<Dish> dishes = new ArrayList<>();
        Double sumW = 0.0;
        while (sumW <= 1) {
            System.out.print("Введите ID блюда:");
            int idD = inInt(sc);

            Dish dish = (Dish) em.find(Dish.class, idD);
            if (dish != null) {
                System.out.println(dish + "   Общий вес " + (String.format("%.3f", sumW + dish.getWeight())) + " кг");
                sumW += dish.getWeight();
                if (sumW <= 1) {
                    dishes.add(dish);
                }
            }
        }

        toScreen("Меню - выбранные блюда:", dishes);
    }

    private void toScreen(String st, List<Dish> dishes) {
        System.out.println("======");
        System.out.println(st);
        System.out.println("------");
        dishes.forEach(m -> System.out.println(m));
        System.out.println("======");
    }

}
