package ru.itmo.sofi.essence.booking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

public final class Booking {
    private final long id;
    private long instrumentId;
    private Instant startAt;
    private Instant endAt;
    private BookingStatus status;
    private String ownerUsername;
    private final Instant createdAt;
    private Instant updatedAt;

//    public Booking(long id, long instrumentId, Instant startAt, Instant endAt, BookingStatus status, String ownerUsername, Instant createdAt, Instant updatedAt) {
//        this.id = id;
//        this.instrumentId = instrumentId;
//        this.startAt = startAt;
//        this.endAt = endAt;
//        this.status = status;
//        this.ownerUsername = ownerUsername;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }

//    public Booking() {
//
//    }

    @JsonCreator
    public Booking (@JsonProperty("id") long id, @JsonProperty("instrumentId") long instrumentId,
                    @JsonProperty("startAt") Instant startAt, @JsonProperty("endAt") Instant endAt,
                    @JsonProperty("status") BookingStatus status, @JsonProperty("ownerUsername") String ownerUsername,
                    @JsonProperty("createdAt") Instant createdAt, @JsonProperty("updatedAt") Instant updatedAt) {
        this.id = id;
        this.instrumentId = instrumentId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
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
        Booking booking = (Booking) o;
        return id == booking.id && instrumentId == booking.instrumentId && Objects.equals(startAt, booking.startAt) && Objects.equals(endAt, booking.endAt) && status == booking.status && Objects.equals(ownerUsername, booking.ownerUsername) && Objects.equals(createdAt, booking.createdAt) && Objects.equals(updatedAt, booking.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instrumentId, startAt, endAt, status, ownerUsername, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", instrumentId=" + instrumentId +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", status=" + status +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
