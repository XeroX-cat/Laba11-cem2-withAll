package ru.itmo.sofi.exception;

public class DatabaseException extends RuntimeException {
  public DatabaseException(String message) {
    super("Ошибка базы: " + message);
  }
}
