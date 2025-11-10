package finance.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private TransactionType type;
    private double amount;
    private String category;
    private String description;
    private LocalDateTime date;

    public Transaction(TransactionType type, String category, double amount, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = LocalDateTime.now();
    }

    // Геттеры
    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }
}