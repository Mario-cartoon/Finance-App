package finance.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private String name;
    private double budgeting;
    private double used;
    
    public Category(String name, double budgeting) {
        this.name = name;
        this.budgeting = budgeting;
        this.used = 0.0;
    }
}
