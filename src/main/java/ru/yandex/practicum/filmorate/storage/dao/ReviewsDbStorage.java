package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.List;

@Component
@Qualifier
@Slf4j
public class ReviewsDbStorage implements ReviewsStorage {

    private final JdbcTemplate jdbcT;
    private static int reviewId = 0;

    public ReviewsDbStorage(JdbcTemplate jdbcT) {
        this.jdbcT = jdbcT;
    }

    @Override
    public Review addReview(Review review) {
        SqlRowSet rowSet = jdbcT.queryForRowSet(
                ""
        );
        return null;
    }

    @Override
    public Review updateReviews(Review review) {
        return null;
    }

    @Override
    public Review getReviewById(int id) {
        return null;
    }

    @Override
    public List<Review> getListReviewsByIdWithLimit(int id, int limit) {
        return null;
    }

    @Override
    public String addLike(int reviewId, int userId) {
        return null;
    }

    @Override
    public String addDislike(int reviewId, int userId) {
        return null;
    }
}
