package ru.itmo.sofi.essence.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.itmo.sofi.exception.UserInputException;

public class User {
    private String login;
    private String password;
    private boolean admin;

    @JsonCreator
    public User(@JsonProperty("login") String login, @JsonProperty("password") String password, @JsonProperty("admin") boolean admin) {
        setLogin(login);
        setPassword(password);
        setAdmin(admin);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setLogin(String login) throws UserInputException {
        if (login != null && !login.isBlank() && login.length() < 33 && login.length() > 6) {
            this.login = login;
        } else {
            throw new UserInputException(login + " не удовлетворяет условию логина.");
        }
    }

    public void setPassword(String password) throws UserInputException {
        if (password != null && !password.isBlank() && password.length() > 3) {
            this.password = password;
        } else {
            throw new UserInputException(password + " не удовлетворяет условию пароля.");
        }
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}