package finance.service;

import finance.models.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.stereotype.Service;

/**
 * Сервис управления финансовыми операциями пользователей
 * Обеспечивает регистрацию, авторизацию, управление транзакциями и бюджетами
 */
@Service
public class ManageService {  
    private Map<String, User> users;
    private User currentUser;
    private final String DATA_FILE = "finance_data.ser";
    
    /**
     * Конструктор сервиса управления
     * Инициализирует хранилище пользователей и загружает данные
     */
    public ManageService() {
        this.users = new HashMap<>();
        loadData();
    }
     
    
    /**
     * Регистрация нового пользователя
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return true если регистрация успешна, false если пользователь уже существует
     */
    public boolean register(String login, String password) {
        if (users.containsKey(login)) {
            return false;
        }
        users.put(login, new User(login, password));
        saveData();
        return true;
    }
    
    /**
     * Авторизация пользователя в системе
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return true если авторизация успешна, false если неверные данные
     */
    public boolean login(String login, String password) {
        User user = users.get(login);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    /**
     * Выход пользователя из системы с сохранением данных
     */
    public void logout() {
        saveData();
        currentUser = null;
    }
    
    /**
     * Получение текущего авторизованного пользователя
     * @return текущий пользователь или null если пользователь не авторизован
     */
    public User getCurrentUser() {
        return currentUser;
    }
     
    
    /**
     * Добавление дохода пользователю
     * @param category категория дохода
     * @param amount сумма дохода
     * @param description описание операции
     * @throws IllegalStateException если пользователь не авторизован
     * @throws IllegalArgumentException если сумма некорректна
     */
    public void addIncome(String category, double amount, String description) {
        checkUserLoggedIn();
        validateAmount(amount);
        
        Transaction transaction = new Transaction(TransactionType.INCOME, category, amount, description);
        currentUser.getWallet().addTransaction(transaction);
        checkAlerts();
    }
    
    /**
     * Добавление расхода пользователю
     * @param category категория расхода
     * @param amount сумма расхода
     * @param description описание операции
     * @throws IllegalStateException если пользователь не авторизован
     * @throws IllegalArgumentException если сумма некорректна
     */
    public void addExpense(String category, double amount, String description) {
        checkUserLoggedIn();
        validateAmount(amount);
        
        Transaction transaction = new Transaction(TransactionType.EXPENSE, category, amount, description);
        currentUser.getWallet().addTransaction(transaction);
        checkAlerts();
    }
    
    /**
     * Установка бюджета для категории расходов
     * @param category категория расходов
     * @param amount сумма бюджета
     * @throws IllegalStateException если пользователь не авторизован
     * @throws IllegalArgumentException если сумма некорректна
     */
    public void setBudget(String category, double amount) {
        checkUserLoggedIn();
        validateAmount(amount);
        
        currentUser.getWallet().setBudget(category, amount);
        checkAlerts();
    }
     
    /**
     * Расчет общего дохода пользователя
     * @return общая сумма доходов
     */
    public double getTotalIncome() {
        return currentUser.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Расчет общего расхода пользователя
     * @return общая сумма расходов
     */
    public double getTotalExpense() {
        return currentUser.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Расчет текущего баланса пользователя
     * @return разница между доходами и расходами
     */
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
    
    /**
     * Получение доходов сгруппированных по категориям
     * @return Map где ключ - категория, значение - сумма доходов
     */
    public Map<String, Double> getIncomeByCategory() {
        return currentUser.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.summingDouble(Transaction::getAmount)
                ));
    }
    
    /**
     * Получение расходов сгруппированных по категориям
     * @return Map где ключ - категория, значение - сумма расходов
     */
    public Map<String, Double> getExpensesByCategory() {
        return currentUser.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.summingDouble(Transaction::getAmount)
                ));
    }
     
    
    /**
     * Перевод средств между пользователями
     * @param toUserLogin логин получателя
     * @param amount сумма перевода
     * @param description описание перевода
     * @return true если перевод выполнен успешно, false в случае ошибки
     */
    public boolean transfer(String toUserLogin, double amount, String description) {
        checkUserLoggedIn();
        validateAmount(amount);
        
        User recipient = users.get(toUserLogin);
        if (recipient == null) {
            System.out.println("Пользователь '" + toUserLogin + "' не найден");
            return false;
        }
        
        if (getBalance() < amount) {
            System.out.println("Недостаточно средств для перевода");
            return false;
        }
        
        this.addExpense("Перевод", amount, "Перевод пользователю: " + toUserLogin + " - " + description);
        recipient.getWallet().addTransaction(
            new Transaction(TransactionType.INCOME, "Перевод", amount, 
                           "Перевод от: " + currentUser.getLogin() + " - " + description)
        );
        
        saveData();
        System.out.println("Перевод пользователю '" + toUserLogin + "' выполнен успешно!");
        return true;
    }
    
    /**
     * Получение последних транзакций пользователя
     * @param count количество транзакций для возврата
     * @return список последних транзакций отсортированных по дате (сначала новые)
     */
    public List<Transaction> getRecentTransactions(int count) {
        return currentUser.getWallet().getTransactions().stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(count)
                .collect(Collectors.toList());
    }
     
    /**
     * Проверка всех типов оповещений
     */
    private void checkAlerts() {
        checkBudgetAlerts();
        checkBalanceAlert();
    }
    
    /**
     * Проверка превышения бюджетов по категориям
     */
    private void checkBudgetAlerts() {
        Map<String, Double> budgets = currentUser.getWallet().getBudgets();
        Map<String, Double> expenses = getExpensesByCategory();
        
        for (Map.Entry<String, Double> budgetEntry : budgets.entrySet()) {
            String category = budgetEntry.getKey();
            double budget = budgetEntry.getValue();
            double spent = expenses.getOrDefault(category, 0.0);
            
            if (spent > budget) {
                System.out.println("ПРЕДУПРЕЖДЕНИЕ: Превышен бюджет по категории '" + category + 
                                 "'! Бюджет: " + budget + ", Потрачено: " + spent);
            } else if (spent > budget * 0.8) {
                System.out.println("ВНИМАНИЕ: Бюджет по категории '" + category + 
                                 "' почти исчерпан! Бюджет: " + budget + ", Потрачено: " + spent);
            }
        }
    }
    
    /**
     * Проверка состояния баланса пользователя
     */
    private void checkBalanceAlert() {
        double balance = getBalance();
        if (balance < 0) {
            System.out.println("КРИТИЧЕСКОЕ ПРЕДУПРЕЖДЕНИЕ: Отрицательный баланс! Баланс: " + balance);
        } else if (balance < 1000) {
            System.out.println("ВНИМАНИЕ: Низкий баланс! Баланс: " + balance);
        }
    }
     
    
    /**
     * Проверка авторизации пользователя
     * @throws IllegalStateException если пользователь не авторизован
     */
    private void checkUserLoggedIn() {
        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
    }
    
    /**
     * Валидация суммы операции
     * @param amount сумма для проверки
     * @throws IllegalArgumentException если сумма некорректна
     */
    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
    }
     
    
    /**
     * Загрузка данных пользователей из файла
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (Map<String, User>) ois.readObject();
            System.out.println("Данные успешно загружены");
        } catch (FileNotFoundException e) {
            System.out.println("Файл данных не найден, создается новый...");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка загрузки данных: " + e.getMessage());
        }
    }
    
    /**
     * Сохранение данных пользователей в файл
     */
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            System.out.println("Данные успешно сохранены");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных: " + e.getMessage());
        }
    }
}