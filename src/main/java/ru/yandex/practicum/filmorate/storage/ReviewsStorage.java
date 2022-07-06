package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsStorage {

    Review addReview(Review review);

    List<Review> getAllReviews();

    Review updateReviews(Review review);

    Review getReviewById(int id);

    List<Review> getListReviewsByFilmIdWithLimit(int id, int limit);

    String addLike(int reviewId, int userId);

    String addDislike(int reviewId, int userId);

    String deleteLike(int reviewId, int userId);

    String deleteDislike(int reviewId, int userId);

    String deleteReviewById(int id);

    boolean checkReviewInDb(int id);

    boolean checkReviewInDb(Review review);

    boolean isHaveUsersLikeOrDislike(int userId);

    List<Review> getReviewByFilmId(int filmId);
}
