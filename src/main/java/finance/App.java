package finance;

import finance.service.ManageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.Scanner;

/**
 *  Класс приложения для управления личными финансами 
 */
@SpringBootApplication
public class App {
    private ManageService financeManager;
    private Scanner scanner;
    private boolean running;
    
    /**
     * Конструктор приложения
     * Инициализирует сервис управления и сканер для ввода данных
     */
    public App() {
        this.financeManager = new ManageService();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    /**
     * Запуск основного цикла приложения
     * Обрабатывает пользовательский ввод и отображает меню
     */
    public void start() {
        System.out.println("_____ Finanse App _____");
        
        while (running) {
            if (financeManager.getCurrentUser() == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
        
        scanner.close();
        System.out.println("Приложение завершено. Данные сохранены.");
    }
    
    /**
     * Отображение меню авторизации
     * Предлагает пользователю войти, зарегистрироваться или выйти
     */
    private void showAuthMenu() {
        System.out.println("\n--- Auth ---");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выйти");
        System.out.print("Выберите действие: ");
        
        int choice = readIntInput();
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                running = false;
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }
    
    /**
     * Отображение главного меню приложения
     * Доступно после успешной авторизации пользователя
     */
    private void showMainMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Установить бюджет");
        System.out.println("4. Показать статистику");
        System.out.println("5. Перевод другому пользователю");
        System.out.println("6. Выйти из аккаунта");
        System.out.print("Выберите действие: ");
        
        int choice = readIntInput();
        
        switch (choice) {
            case 1:
                addIncome();
                break;
            case 2:
                addExpense();
                break;
            case 3:
                setBudget();
                break;
            case 4:
                showStatistics();
                break;
            case 5:
                transferMoney();
                break;
            case 6:
                financeManager.logout();
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }
    
    /**
     * Авторизация пользователя в системе
     * Запрашивает логин и пароль, проверяет их корректность
     */
    private void login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();
        
        if (financeManager.login(login, password)) {
            System.out.println("Успешный вход! Добро пожаловать, " + login);
        } else {
            System.out.println("Неверный логин или пароль!");
        }
    }
    
    /**
     * Регистрация нового пользователя
     * Создает новую учетную запись с указанными логином и паролем
     */
    private void register() {
        System.out.print("Придумайте логин: ");
        String login = scanner.nextLine();
        System.out.print("Придумайте пароль: ");
        String password = scanner.nextLine();
        
        if (financeManager.register(login, password)) {
            System.out.println("Регистрация успешна! Теперь войдите в систему.");
        } else {
            System.out.println("Пользователь с таким логином уже существует!");
        }
    }
    
    /**
     * Добавление новой доходной операции
     * Запрашивает категорию, сумму и описание дохода
     */
    private void addIncome() {
        try {
            System.out.print("Категория дохода: ");
            String category = scanner.nextLine();
            System.out.print("Сумма: ");
            double amount = readDoubleInput();
            System.out.print("Описание: ");
            String description = scanner.nextLine();
            
            financeManager.addIncome(category, amount, description);
            System.out.println("Доход успешно добавлен!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    /**
     * Добавление новой расходной операции
     * Запрашивает категорию, сумму и описание расхода
     */
    private void addExpense() {
        try {
            System.out.print("Категория расхода: ");
            String category = scanner.nextLine();
            System.out.print("Сумма: ");
            double amount = readDoubleInput();
            System.out.print("Описание: ");
            String description = scanner.nextLine();
            
            financeManager.addExpense(category, amount, description);
            System.out.println("Расход успешно добавлен!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    /**
     * Установка бюджета для категории расходов
     * Определяет максимальную сумму расходов для указанной категории
     */
    private void setBudget() {
        try {
            System.out.print("Категория для бюджета: ");
            String category = scanner.nextLine();
            System.out.print("Сумма бюджета: ");
            double amount = readDoubleInput();
            
            financeManager.setBudget(category, amount);
            System.out.println("Бюджет установлен!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    /**
     * Отображение финансовой статистики пользователя
     * Показывает общие суммы, распределение по категориям и состояние бюджетов
     */
    private void showStatistics() {
        System.out.println("\n--- Статистика ---");
        
        displayGeneralStatistics();
        displayIncomeByCategory();
        displayExpensesByCategory();
        displayBudgets();
    }
    
    /**
     * Отображение общей финансовой статистики
     * Включает общий доход, расходы и баланс
     */
    private void displayGeneralStatistics() {
        double totalIncome = financeManager.getTotalIncome();
        double totalExpense = financeManager.getTotalExpense();
        double balance = totalIncome - totalExpense;
        
        System.out.printf("Общий доход: %,10.2f%n", totalIncome);
        System.out.printf("Общие расходы: %,10.2f%n", totalExpense);
        System.out.printf("Баланс: %,10.2f%n", balance);
    }
    
    /**
     * Отображение доходов по категориям
     * Показывает распределение доходов между различными категориями
     */
    private void displayIncomeByCategory() {
        System.out.println("\nДоходы по категориям:");
        Map<String, Double> incomeByCategory = financeManager.getIncomeByCategory();
        if (incomeByCategory.isEmpty()) {
            System.out.println("  Нет данных о доходах");
        } else {
            incomeByCategory.forEach((category, amount) -> 
                System.out.printf("  %s: %,10.2f%n", category, amount));
        }
    }
    
    /**
     * Отображение расходов по категориям
     * Показывает распределение расходов между различными категориями
     */
    private void displayExpensesByCategory() {
        System.out.println("\nРасходы по категориям:");
        Map<String, Double> expensesByCategory = financeManager.getExpensesByCategory();
        if (expensesByCategory.isEmpty()) {
            System.out.println("  Нет данных о расходах");
        } else {
            expensesByCategory.forEach((category, amount) -> 
                System.out.printf("  %s: %,10.2f%n", category, amount));
        }
    }
    
    /**
     * Отображение информации о бюджетах
     * Показывает установленные бюджеты, фактические расходы и остатки
     */
    private void displayBudgets() {
        System.out.println("\nБюджеты по категориям:");
        Map<String, Double> budgets = financeManager.getCurrentUser().getWallet().getBudgets();
        Map<String, Double> expenses = financeManager.getExpensesByCategory();
        
        if (budgets.isEmpty()) {
            System.out.println("  Бюджеты не установлены");
        } else {
            budgets.forEach((category, budget) -> {
                double spent = expenses.getOrDefault(category, 0.0);
                double remaining = budget - spent;
                System.out.printf("  %s: Бюджет: %,10.2f, Потрачено: %,10.2f, Осталось: %,10.2f%n", 
                    category, budget, spent, remaining);
            });
        }
    }
    
    /**
     * Выполнение перевода средств другому пользователю
     * Запрашивает логин получателя, сумму и описание перевода
     */
    private void transferMoney() {
        try {
            System.out.print("Логин получателя: ");
            String toUser = scanner.nextLine();
            System.out.print("Сумма перевода: ");
            double amount = readDoubleInput();
            System.out.print("Описание: ");
            String description = scanner.nextLine();
            
            if (financeManager.transfer(toUser, amount, description)) {
                System.out.println("Перевод выполнен успешно!");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    /**
     * Чтение целочисленного ввода от пользователя
     * Повторяет запрос до получения корректного целого числа
     * @return введенное пользователем целое число
     */
    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }
    
    /**
     * Чтение числового ввода от пользователя
     * Повторяет запрос до получения корректного числа с плавающей точкой
     * @return введенное пользователем число
     */
    private double readDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }
    
    /**
     * Точка входа в приложение
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        new App().start();
    }
}