package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!checkReviewInDb(review)) {
            reviewId = getReviewLastId() + 1;
            review.setId(reviewId);
            jdbcT.update(
                    "INSERT INTO REVIEWS (REVIEWS_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
                    , review.getId(), review.getContent(), review.getIsPositive(), review.getUserId(),
                    review.getFilmId(), review.getUseful()
            );
            return review;
        } else {
            throw new ValidationException("Ошибка создания отзыва");
        }
    }

    @Override
    public List<Review> getAllReviews() {
        List<Review> reviewList = new ArrayList<>();
        SqlRowSet rowSet;
        rowSet = jdbcT.queryForRowSet(
                "SELECT * FROM REVIEWS"
        );
        while (rowSet.next()) {
            reviewList.add(makeReview(rowSet));
        }
        return reviewList.stream().sorted((o1, o2) -> o2.getUseful() - o1.getUseful()).collect(Collectors.toList());
    }

    @Override
    public Review updateReviews(Review review) {
        jdbcT.update(
                "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ?" +
                        "WHERE REVIEWS_ID = ?",
                review.getContent(), review.getIsPositive(), review.getId()
        );
        return review;
    }

    @Override
    public Review getReviewById(int id) {
        SqlRowSet rowSet = jdbcT.queryForRowSet(
                "SELECT * FROM REVIEWS WHERE REVIEWS_ID = ?", id
        );
        if (rowSet.next()) {
            return makeReview(rowSet);
        } else {
            throw new NotFoundException(String.format("Ревью с ID:%s не найден", id));
        }
    }

    @Override
    public List<Review> getListReviewsByFilmIdWithLimit(int filmId, int limit) {
        List<Review> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcT.queryForRowSet(
                "SELECT * FROM REVIEWS WHERE FILM_ID = ? LIMIT ?",
                filmId, limit
        );
        while (rowSet.next()) {
            result.add(makeReview(rowSet));
        }
        return result;
    }

    @Override
    public String addLike(int reviewId, int userId) {
        jdbcT.update(
                "UPDATE REVIEWS_LIKES SET IS_POSITIVE = TRUE, USER_ID = ? WHERE REVIEWS_ID = ? ",
                userId, reviewId
        );
        jdbcT.update(
                "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEWS_ID = ?", reviewId
        );
        return String.format("Лайк пользователем с ID:%s на отзыв с ID:%s добавлен", userId, reviewId);
    }

    @Override
    public String addDislike(int reviewId, int userId) {
        jdbcT.update(
                "UPDATE REVIEWS_LIKES SET IS_POSITIVE = FALSE, USER_ID = ? WHERE REVIEWS_ID = ?",
                userId, reviewId
        );
        jdbcT.update(
                "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEWS_ID = ?", reviewId
        );
        return String.format("Дизлайк пользователем с ID:%s на отзыв с ID:%s добавлен", userId, reviewId);
    }

    @Override
    public String deleteLike(int reviewId, int userId) {
        jdbcT.update(
                "DELETE FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEWS_ID = ? AND IS_POSITIVE = TRUE",
                userId, reviewId
        );
        jdbcT.update(
                "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEWS_ID = ?", reviewId
        );
        return String.format("Лайк пользователем с ID:%s на отзыв с ID:%s удален", userId, reviewId);
    }

    @Override
    public String deleteDislike(int reviewId, int userId) {
        jdbcT.update(
                "DELETE FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEWS_ID = ? AND IS_POSITIVE = FALSE",
                userId, reviewId
        );
        jdbcT.update(
                "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEWS_ID = ?", reviewId
        );
        return String.format("Дизлайк пользователем с ID:%s на отзыв с ID:%s удален", userId, reviewId);
    }

    @Override
    public String deleteReviewById(int reviewId) {
        jdbcT.update(
                "DELETE FROM REVIEWS WHERE REVIEWS_ID = ?", reviewId
        );
        return String.format("Отзыв с ID:%s удален", reviewId);
    }

    @Override
    public boolean checkReviewInDb(int id) {
        if (id != 0) {
            return (jdbcT.queryForRowSet("SELECT * FROM REVIEWS WHERE REVIEWS_ID = ?", id).next());
        } else {
            return false;
        }
    }

    @Override
    public boolean checkReviewInDb(Review review) {
        return (jdbcT.queryForRowSet("SELECT * FROM REVIEWS WHERE REVIEWS_ID = ? " +
                        "AND  CONTENT = ? AND  USER_ID = ? AND FILM_ID = ?",
                review.getId(), review.getContent(), review.getUserId(), review.getFilmId()).next());
    }

    @Override
    public List<Review> getReviewByFilmId(int filmId) {
        return getAllReviews().stream().filter(review -> review.getFilmId() == filmId).collect(Collectors.toList());
    }

    @Override
    public boolean isHaveUsersLikeOrDislike(int userId) {
        return jdbcT.queryForRowSet("SELECT * FROM REVIEWS_LIKES WHERE USER_ID = ?", userId).next();
    }

    private Review makeReview(SqlRowSet rowSet) {
        return Review.builder()
                .id(rowSet.getInt("REVIEWS_ID"))
                .content(rowSet.getString("CONTENT"))
                .isPositive(rowSet.getBoolean("IS_POSITIVE"))
                .userId(rowSet.getInt("USER_ID"))
                .filmId(rowSet.getInt("FILM_ID"))
                .useful(rowSet.getInt("USEFUL"))
                .build();
    }

    private int getReviewLastId() {
        SqlRowSet rowSet = jdbcT.queryForRowSet(
                "SELECT REVIEWS_ID AS COUNT FROM REVIEWS ORDER BY COUNT DESC "
        );
        if (rowSet.first()) {
            return rowSet.getInt("COUNT");
        } else {
            return 0;
        }
    }
}
