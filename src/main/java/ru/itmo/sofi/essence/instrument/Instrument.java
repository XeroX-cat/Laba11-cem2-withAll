package ru.itmo.sofi.essence.instrument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.itmo.sofi.exception.UserInputException;

import java.time.Instant;
import java.util.Objects;

public final class Instrument {
    private final long id;
    private String name;
    private final InstrumentType type;
    private String inventoryNumber;
    private String location;
    private InstrumentStatus status;
    private String ownerUsername;
    private final Instant createdAt;
    private Instant updatedAt;

//    public Instrument(long id, String name, InstrumentType type, String inventoryNumber, String location, InstrumentStatus status, String ownerUsername, Instant createdAt, Instant updatedAt) {
//        this.id = id;
//        this.setName(name);
//        this.type = type;
//        this.setInventoryNumber(inventoryNumber);
//        this.setLocation(location);
//        this.status = status;
//        this.ownerUsername = ownerUsername;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }

    @JsonCreator
    public Instrument(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("type") InstrumentType type,
                      @JsonProperty("inventoryNumber") String inventoryNumber, @JsonProperty("location") String location,
                      @JsonProperty("status") InstrumentStatus status, @JsonProperty("ownerUsername") String ownerUsername,
                      @JsonProperty("createdAt") Instant createdAt, @JsonProperty("updatedAt") Instant updatedAt) {
        this.id = id;
        this.setName(name);
        this.type = type;
        this.setInventoryNumber(inventoryNumber);
        this.setLocation(location);
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws UserInputException {
        if (name != null && !name.isEmpty() && name.length() < 128) {
            this.name = name;
        } else {
            throw new UserInputException(name + "не удовлетворяет условию имени.");
        }

    }

    public InstrumentType getType() {
        return type;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) throws UserInputException {
        if (inventoryNumber == null || inventoryNumber.length() < 32) {
            this.inventoryNumber = inventoryNumber;
        } else {
            throw new UserInputException(inventoryNumber + "не удовлетворяет условию инвентарного номера.");
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) throws UserInputException {
        if (location != null && !location.isEmpty() && location.length() < 64) {
            this.location = location;
        } else {
            throw new UserInputException(location + "не удовлетворяет условию локации.");
        }
    }

    public InstrumentStatus getStatus() {
        return status;
    }

    public void setStatus(InstrumentStatus status) {
        this.status = status;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Instrument that = (Instrument) o;
        return id == that.id && Objects.equals(name, that.name) && type == that.type && Objects.equals(inventoryNumber, that.inventoryNumber) && Objects.equals(location, that.location) && status == that.status && Objects.equals(ownerUsername, that.ownerUsername) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, inventoryNumber, location, status, ownerUsername, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", inventoryNumber='" + inventoryNumber + '\'' +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
