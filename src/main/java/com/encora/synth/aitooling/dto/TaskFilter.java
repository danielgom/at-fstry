package com.encora.synth.aitooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskFilter {

    private String orderBy;

    private String orderType;

    private Integer pageNumber;

    private Integer pageSize;

    private TaskStatus status;

    public boolean allNull() {
        return Stream.of(this.orderBy, this.orderType,
                        this.status, this.pageNumber, this.pageSize)
                .allMatch(Objects::isNull);
    }
}
