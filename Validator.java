package attendance;

// Input validation for username and password
class Validator {
    static String validateUsername(String username) {
        if (username == null || username.isEmpty())
            return "Username is required.";
        if (username.length() < 5)
            return "Username must be at least 5 characters.";
        if (username.contains(" "))
            return "Username must not contain spaces.";
        return null;
    }

    static String validatePassword(String password) {
        if (password == null || password.isEmpty())
            return "Password is required.";
        if (password.length() < 8)
            return "Password must be at least 8 characters.";
        if (!password.matches(".*[A-Z].*"))
            return "Password must contain at least 1 uppercase letter.";
        if (!password.matches(".*[0-9].*"))
            return "Password must contain at least 1 number.";
        return null;
    }

    static String buildErrorMessage(String usernameErr, String passwordErr) {
        StringBuilder sb = new StringBuilder("<html>");
        if (usernameErr != null) sb.append("<b>Username:</b> ").append(usernameErr).append("<br>");
        if (passwordErr != null) sb.append("<b>Password:</b> ").append(passwordErr);
        sb.append("</html>");
        return sb.toString();
    }
}