package com.intelliflow.modules.task.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing Directed Task Dependency Links.
 * 
 * Defines dependency relationships where `blockingTask` must reach COMPLETED
 * state before `dependentTask` can proceed to IN_PROGRESS.
 */
@Entity
@Table(name = "task_dependencies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDependencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocking_task_id", nullable = false)
    private TaskEntity blockingTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_task_id", nullable = false)
    private TaskEntity dependentTask;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
