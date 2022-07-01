package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsStorage {

    public Review addReview(Review review);

    public Review updateReviews(Review review);

    public Review getReviewById(int id);

    public List<Review> getListReviewsByIdWithLimit(int id, int limit);

    public String addLike(int reviewId, int userId);

    public String addDislike(int reviewId, int userId);
}
