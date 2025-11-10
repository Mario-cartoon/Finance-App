package finance.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Wallet implements Serializable {
    private Map<String, Double> budgets;
    private List<Transaction> transactions;
    
    public Wallet() {
        this.budgets = new HashMap<>();
        this.transactions = new ArrayList<>();
    }
     
    public Map<String, Double> getBudgets() { return new HashMap<>(budgets); }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public void setBudget(String category, double amount) { budgets.put(category, amount); }
    public void addTransaction(Transaction transaction) { transactions.add(transaction); }
    
    public boolean removeBudget(String category) {
        return budgets.remove(category) != null;
    } 
    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }
    
    public List<Transaction> getTransactionsByType(TransactionType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type) {
                result.add(transaction);
            }
        }
        return result;
    }
    
    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equals(category)) {
                result.add(transaction);
            }
        }
        return result;
    }
     
    public double getTotalIncome() {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                total += transaction.getAmount();
            }
        }
        return total;
    }
    
    public double getTotalExpenses() {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                total += transaction.getAmount();
            }
        }
        return total;
    }
    
    public double getBalance() {
        return getTotalIncome() - getTotalExpenses();
    }
    
    public double getSpentByCategory(String category) {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE && 
                transaction.getCategory().equals(category)) {
                total += transaction.getAmount();
            }
        }
        return total;
    }
    
    public double getRemainingBudget(String category) {
        Double budget = budgets.get(category);
        if (budget == null) {
            return 0;
        }
        return budget - getSpentByCategory(category);
    }
    
    @Override
    public String toString() {
        return String.format("Wallet{balance=%.2f, budgets=%d, transactions=%d}", 
                getBalance(), budgets.size(), transactions.size());
    }
}