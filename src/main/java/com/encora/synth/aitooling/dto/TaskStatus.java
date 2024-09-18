package com.encora.synth.aitooling.dto;

import lombok.Getter;

@Getter
public enum TaskStatus {
    NONE("none"),
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");

    private final String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public static TaskStatus fromString(String status) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getStatus().equalsIgnoreCase(status)) {
                return taskStatus;
            }
        }
        return TaskStatus.NONE;
    }
}
