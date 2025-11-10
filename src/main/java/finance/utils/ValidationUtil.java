package finance.utils;
public class ValidationUtil {
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }
    
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public static boolean isValidLogin(String login) {
        return login != null && login.length() >= 3 && login.matches("[a-zA-Z0-9]+");
    }
}