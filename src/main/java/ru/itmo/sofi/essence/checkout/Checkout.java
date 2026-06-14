package ru.itmo.sofi.essence.checkout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.itmo.sofi.exception.UserInputException;

import java.time.Instant;
import java.util.Objects;

public final class Checkout {
    private final long id;
    private long instrumentId;
    private String username;
    private String comment;
    private Instant takenAt;
    private Instant returnedAt;
    private ReturnCondition returnCondition;
    private String ownerUsername;
    private final Instant createdAt;

//    public Checkout(long id, long instrumentId, String username, String comment, Instant takenAt, Instant returnedAt, ReturnCondition returnCondition, String ownerUsername, Instant createdAt) {
//        this.id = id;
//        this.instrumentId = instrumentId;
//        this.setUsername(username);
//        this.setComment(comment);
//        this.takenAt = takenAt;
//        this.returnedAt = returnedAt;
//        this.returnCondition = returnCondition;
//        this.ownerUsername = ownerUsername;
//        this.createdAt = createdAt;
//    }

    @JsonCreator
    public Checkout(@JsonProperty("id") long id, @JsonProperty("instrumentId") long instrumentId,
                    @JsonProperty("username") String username, @JsonProperty("comment") String comment,
                    @JsonProperty("takenAt") Instant takenAt, @JsonProperty("returnedAt") Instant returnedAt,
                    @JsonProperty("returnCondition") ReturnCondition returnCondition, @JsonProperty("ownerUsername") String ownerUsername,
                    @JsonProperty("createdAt") Instant createdAt) {
        this.id = id;
        this.instrumentId = instrumentId;
        this.setUsername(username);
        this.setComment(comment);
        this.takenAt = takenAt;
        this.returnedAt = returnedAt;
        this.returnCondition = returnCondition;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws UserInputException {
        if (username != null && !username.trim().isEmpty() && username.trim().length() < 64) {
            this.username = username;
        } else {
            throw new UserInputException(username + " не удовлетворяет условию имени пользователя.");
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) throws UserInputException {
        if (comment == null || comment.trim().length() < 128) {
            this.comment = comment;
        } else {
            throw new UserInputException(comment + " не удовлетворяет условию коммита.");
        }
    }

    public Instant getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Instant takenAt) {
        this.takenAt = takenAt;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    public ReturnCondition getReturnCondition() {
        return returnCondition;
    }

    public void setReturnCondition(ReturnCondition returnCondition) {
        this.returnCondition = returnCondition;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Checkout checkout = (Checkout) o;
        return id == checkout.id && instrumentId == checkout.instrumentId && Objects.equals(username, checkout.username) && Objects.equals(comment, checkout.comment) && Objects.equals(takenAt, checkout.takenAt) && Objects.equals(returnedAt, checkout.returnedAt) && returnCondition == checkout.returnCondition && Objects.equals(ownerUsername, checkout.ownerUsername) && Objects.equals(createdAt, checkout.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instrumentId, username, comment, takenAt, returnedAt, returnCondition, ownerUsername, createdAt);
    }

    @Override
    public String toString() {
        return "Checkout{" +
                "id=" + id +
                ", instrumentId=" + instrumentId +
                ", username='" + username + '\'' +
                ", comment='" + comment + '\'' +
                ", takenAt=" + takenAt +
                ", returnedAt=" + returnedAt +
                ", returnCondition=" + returnCondition +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
