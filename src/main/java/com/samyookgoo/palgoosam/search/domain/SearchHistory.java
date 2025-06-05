package com.samyookgoo.palgoosam.search.domain;

import com.samyookgoo.palgoosam.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "search_history")
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(length = 100)
    private String keyword;

    @Column(name = "search_count")
    @ColumnDefault("1")
    private Long searchCount = 1L;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    public void restoreAndIncrement() {
        this.searchCount++;
        this.isDeleted = false;
    }

    public void softDeleteSearchHistory() {
        this.isDeleted = true;
    }

    public Boolean hasPermission(Long userId) {
        return this.getUser().getId().equals(userId);
    }
}
