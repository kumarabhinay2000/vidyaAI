package org.vidyaai.quizservices.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vidyaai.quizservices.entity.Leaderboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {

    Optional<Leaderboard> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT l FROM Leaderboard l ORDER BY l.totalPoints DESC, l.avgScore DESC")
    List<Leaderboard> findTopPlayers(Pageable pageable);

    @Query("SELECT l FROM Leaderboard l WHERE l.userId IN :userIds")
    List<Leaderboard> findByUserIds(@Param("userIds") List<UUID> userIds);

    @Query("SELECT MAX(l.totalPoints) FROM Leaderboard l")
    Integer maxTotalPoints();
}