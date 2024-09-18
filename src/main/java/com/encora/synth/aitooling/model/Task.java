package com.encora.synth.aitooling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("due_date")
    private LocalDateTime dueDate;

    @Field("status")
    private String status;

    @Field("user_id")
    private String userID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(dueDate, task.dueDate) && Objects.equals(status, task.status) &&
                Objects.equals(userID, task.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, dueDate, status, userID);
    }
}
