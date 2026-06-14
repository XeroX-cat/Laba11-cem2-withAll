package ru.itmo.sofi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.itmo.sofi.base.CollectionStorage;
import ru.itmo.sofi.bstorage.UserBas;
import ru.itmo.sofi.essence.user.User;
import ru.itmo.sofi.exception.DatabaseException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.login.SafePassword;
import ru.itmo.sofi.base.JsonCollectionStorage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class UserService {
    private final UserBas userBas = new UserBas();
    ;
//    private static final Map<String, User> users = new HashMap<>();
//    private final CollectionStorage<User> userStorage = new JsonCollectionStorage<>(new TypeReference<Set<User>>() {
//    });
//    private Path usersPath;

//    public void registration(String login, String password, boolean admin) throws UserInputException { - хороший без бд
//        if (users.containsKey(login)) {
//            throw new UserInputException("Пользователь с таким логином уже существует.");
//        }
//        User user = new User(login, password, admin);
//        String passwordHash = SafePassword.hash(password);
//        user.setPassword(passwordHash);
//        users.put(login, user);
//    }

    public void registration(String login, String password, boolean admin) throws UserInputException {
        try {
            if (userBas.existsByLogin(login)) {
                throw new UserInputException("Пользователь с таким логином уже существует.");
            }
            User user = new User(login, password, admin);
            String passwordHash = SafePassword.hash(password);
            user.setPassword(passwordHash);
            userBas.save(user);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при регистрации пользователя.");
        }
    }

//    public User login(String login, String password) throws UserInputException { - хороший без бд
//        User user = users.get(login);
//        if (user == null) {
//            throw new UserInputException("Такого пользователя нет.");
//        }
//        String passwordHash = SafePassword.hash(password);
//        if (!user.getPassword().equals(passwordHash)) {
//            throw new UserInputException("Неверный пароль.");
//        }
//        return user;
//    }

    public User login(String login, String password) throws UserInputException {
        try {
            User user = userBas.findByLogin(login);
            if (user == null) {
                throw new UserInputException("Такого пользователя нет.");
            }
            String passwordHash = SafePassword.hash(password);
            if (!user.getPassword().equals(passwordHash)) {
                throw new UserInputException("Неверный пароль.");
            }
            return user;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при входе.");
        }
    }

//    public User findUser(String login) { - хороший без бд
//        return users.get(login);
//    }
    public User findUser(String login) {
        try {
            return userBas.findByLogin(login);
        } catch (DatabaseException e) {
            return null;
        }
    }
//    public Collection<User> getAllUsers() { - хороший без бд
//        return users.values();
//    }
    public Collection<User> getAllUsers() {
        try {
            return userBas.findAll();
        } catch (DatabaseException e) {
            return new HashSet<>();
        }
    }


//    public void resetPassword(String login, String password, boolean admin) throws UserInputException { - хороший без бд
//        User user = users.get(login);
//        if (user == null) {
//            throw new UserInputException("Такого пользователя нет.");
//        }
//        if (password == null || password.isBlank() || password.length() < 4) {
//            throw new UserInputException("Пароль должен быть не менее 4 символов.");
//        }
//        User newUser = new User(login, password, admin);
//        String passwordHash = SafePassword.hash(password);
//        user.setPassword(passwordHash);
//    }

    public void resetPassword(String login, String password, boolean admin) throws UserInputException {
        try {
            User user = userBas.findByLogin(login);
            if (user == null) {
                throw new UserInputException("Такого пользователя нет.");
            }
            if (password == null || password.isBlank() || password.length() < 4) {
                throw new UserInputException("Пароль должен быть не менее 4 символов.");
            }
            String passwordHash = SafePassword.hash(password);
            user.setPassword(passwordHash);
            user.setAdmin(admin);
            userBas.update(user);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при сбросе пароля.");
        }
    }

//    public void saveUsers(Path folderPath) {
//        userStorage.save(new HashSet<>(users.values()), folderPath.resolve("users.json"));
//    }

//    public void saveUsers(Path folderPath) { - хороший без бд
//        try {
//            if (!Files.exists(folderPath)) {
//                Files.createDirectories(folderPath);
//            }
//
//            Path usersFile = folderPath.resolve("users.json");
//
//            userStorage.save(new HashSet<>(users.values()), usersFile);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void loadUsers(Path folderPath) {
//        Path usersPath = folderPath.resolve("users.json");
//        if (!Files.exists(usersPath)) {
//            return;
//        }
//        Set<User> loadedUsers = userStorage.load(usersPath);
//        users.clear();
//        for (User user : loadedUsers) {
//            users.put(user.getLogin(), user);
//        }
//    }
//    public void loadUsers(Path folderPath) { - хороший без бд
//        try {
//            if (!Files.exists(folderPath)) {
//                Files.createDirectories(folderPath);
//                users.clear();
//                return;
//            }
//            Path usersFile = folderPath.resolve("users.json");
//            if (!Files.exists(usersFile)) {
//                users.clear();
//                saveUsers(folderPath);
//                return;
//            }
//            Set<User> loadedUsers = userStorage.load(usersFile);
//            users.clear();
//            for (User user : loadedUsers) {
//                users.put(user.getLogin(), user);
//            }
//        } catch (Exception e) {
//            users.clear();
//        }
//    }

//    public Set<User> getAll() { - хороший без бд
//        return new HashSet<>(users.values());
//    }

    public Set<User> getAll() {
        return new HashSet<>(getAllUsers());
    }

    //    public void doAdmin (String login) throws UserInputException { - хороший без бд
//        User user = users.get(login);
//        if (user == null) {
//            throw new UserInputException("Пользователь не найден.");
//        }
//        user.setAdmin(!user.getAdmin());
//    }
    public void doAdmin(String login) throws UserInputException {
        try {
            User user = userBas.findByLogin(login);
            if (user == null) {
                throw new UserInputException("Пользователь не найден.");
            }
            user.setAdmin(!user.getAdmin());
            userBas.update(user);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при изменении прав пользователя.");
        }
    }
}
