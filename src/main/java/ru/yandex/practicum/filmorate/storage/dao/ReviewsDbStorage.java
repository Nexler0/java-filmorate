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

    private static final String INSERT_REVIEW_SQL =
            "INSERT INTO REVIEWS (REVIEWS_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ALL_REVIEWS = "SELECT * FROM REVIEWS";
    private static final String UPDATE_REVIEW_BY_ID = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ?" +
            "WHERE REVIEWS_ID = ?";
    private static final String GET_REVIEW_BY_ID = "SELECT * FROM REVIEWS WHERE REVIEWS_ID = ?";
    private static final String GET_REVIEW_BY_ID_WITH_LIMIT = "SELECT * FROM REVIEWS WHERE FILM_ID = ? LIMIT ?";
    private static final String UPDATE_REVIEWS_LIKES_TO_POSITIVE_SQL =
            "UPDATE REVIEWS_LIKES SET IS_POSITIVE = TRUE, USER_ID = ? WHERE REVIEWS_ID = ? ";
    private static final String UPDATE_REVIEW_USEFUL_TO_POSITIVE_SQL =
            "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEWS_ID = ?";
    private static final String UPDATE_REVIEWS_LIKES_TO_NEGATIVE_SQL =
            "UPDATE REVIEWS_LIKES SET IS_POSITIVE = FALSE, USER_ID = ? WHERE REVIEWS_ID = ? ";
    private static final String UPDATE_REVIEW_USEFUL_TO_NEGATIVE_SQL =
            "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEWS_ID = ?";
    private static final String DELETE_REVIEWS_LIKES_BY_USER_ID_AND_ID_POSITIVE_SQL =
            "DELETE FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEWS_ID = ? AND IS_POSITIVE = TRUE";
    private static final String DELETE_REVIEWS_LIKES_BY_USER_ID_AND_ID_NEGATIVE_SQL =
            "DELETE FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEWS_ID = ? AND IS_POSITIVE = FALSE";
    private static final String DELETE_REVIEW_BY_ID_SQL = "DELETE FROM REVIEWS WHERE REVIEWS_ID = ?";
    private static final String CHECK_REVIEWS_IN_DB_SQL = "SELECT * FROM REVIEWS WHERE REVIEWS_ID = ? " +
            "AND  CONTENT = ? AND  USER_ID = ? AND FILM_ID = ?";
    private static final String GET_REVIEW_LIKES_BY_USER_ID_SQL = "SELECT * FROM REVIEWS_LIKES WHERE USER_ID = ?";
    private static final String GET_LAST_REVIEWS_ID_SQL = "SELECT REVIEWS_ID AS COUNT FROM REVIEWS ORDER BY COUNT DESC ";

    public ReviewsDbStorage(JdbcTemplate jdbcT) {
        this.jdbcT = jdbcT;
    }

    @Override
    public Review addReview(Review review) {
        if (!checkReviewInDb(review)) {
            reviewId = getReviewLastId() + 1;
            review.setId(reviewId);
            jdbcT.update(
                    INSERT_REVIEW_SQL, review.getId(), review.getContent(), review.getIsPositive(), review.getUserId(),
                    review.getFilmId(), review.getUseful());
            return review;
        } else {
            throw new ValidationException("Ошибка создания отзыва");
        }
    }

    @Override
    public List<Review> getAllReviews() {
        List<Review> reviewList = new ArrayList<>();
        SqlRowSet rowSet;
        rowSet = jdbcT.queryForRowSet(GET_ALL_REVIEWS);
        while (rowSet.next()) {
            reviewList.add(makeReview(rowSet));
        }
        return reviewList.stream().sorted((o1, o2) -> o2.getUseful() - o1.getUseful()).collect(Collectors.toList());
    }

    @Override
    public Review updateReviews(Review review) {
        jdbcT.update(UPDATE_REVIEW_BY_ID, review.getContent(), review.getIsPositive(), review.getId());
        return review;
    }

    @Override
    public Review getReviewById(int id) {
        SqlRowSet rowSet = jdbcT.queryForRowSet(GET_REVIEW_BY_ID, id);
        if (rowSet.next()) {
            return makeReview(rowSet);
        } else {
            throw new NotFoundException(String.format("Ревью с ID:%s не найден", id));
        }
    }

    @Override
    public List<Review> getListReviewsByFilmIdWithLimit(int filmId, int limit) {
        List<Review> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcT.queryForRowSet(GET_REVIEW_BY_ID_WITH_LIMIT, filmId, limit);
        while (rowSet.next()) {
            result.add(makeReview(rowSet));
        }
        return result;
    }

    @Override
    public String addLike(int reviewId, int userId) {
        jdbcT.update(UPDATE_REVIEWS_LIKES_TO_POSITIVE_SQL, userId, reviewId);
        jdbcT.update(UPDATE_REVIEW_USEFUL_TO_POSITIVE_SQL, reviewId);
        return String.format("Лайк пользователем с ID:%s на отзыв с ID:%s добавлен", userId, reviewId);
    }

    @Override
    public String addDislike(int reviewId, int userId) {
        jdbcT.update(UPDATE_REVIEWS_LIKES_TO_NEGATIVE_SQL, userId, reviewId);
        jdbcT.update(UPDATE_REVIEW_USEFUL_TO_NEGATIVE_SQL, reviewId);
        return String.format("Дизлайк пользователем с ID:%s на отзыв с ID:%s добавлен", userId, reviewId);
    }

    @Override
    public String deleteLike(int reviewId, int userId) {
        jdbcT.update(DELETE_REVIEWS_LIKES_BY_USER_ID_AND_ID_POSITIVE_SQL, userId, reviewId);
        jdbcT.update(UPDATE_REVIEW_USEFUL_TO_NEGATIVE_SQL, reviewId);
        return String.format("Лайк пользователем с ID:%s на отзыв с ID:%s удален", userId, reviewId);
    }

    @Override
    public String deleteDislike(int reviewId, int userId) {
        jdbcT.update(DELETE_REVIEWS_LIKES_BY_USER_ID_AND_ID_NEGATIVE_SQL, userId, reviewId);
        jdbcT.update(UPDATE_REVIEW_USEFUL_TO_POSITIVE_SQL, reviewId);
        return String.format("Дизлайк пользователем с ID:%s на отзыв с ID:%s удален", userId, reviewId);
    }

    @Override
    public String deleteReviewById(int reviewId) {
        jdbcT.update(DELETE_REVIEW_BY_ID_SQL, reviewId);
        return String.format("Отзыв с ID:%s удален", reviewId);
    }

    @Override
    public boolean checkReviewInDb(int id) {
        if (id != 0) {
            return (jdbcT.queryForRowSet(GET_REVIEW_BY_ID, id).next());
        } else {
            return false;
        }
    }

    @Override
    public boolean checkReviewInDb(Review review) {
        return (jdbcT.queryForRowSet(CHECK_REVIEWS_IN_DB_SQL, review.getId(), review.getContent(),
                review.getUserId(), review.getFilmId()).next());
    }

    @Override
    public List<Review> getReviewByFilmId(int filmId) {
        return getAllReviews().stream().filter(review -> review.getFilmId() == filmId).collect(Collectors.toList());
    }

    @Override
    public boolean isHaveUsersLikeOrDislike(int userId) {
        return jdbcT.queryForRowSet(GET_REVIEW_LIKES_BY_USER_ID_SQL, userId).next();
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
        SqlRowSet rowSet = jdbcT.queryForRowSet(GET_LAST_REVIEWS_ID_SQL);
        if (rowSet.first()) {
            return rowSet.getInt("COUNT");
        } else {
            return 0;
        }
    }
}
