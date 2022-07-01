package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.List;

@Service
public class ReviewsService {

    private final ReviewsStorage reviewsStorage;

    public ReviewsService(ReviewsStorage reviewsStorage) {
        this.reviewsStorage = reviewsStorage;
    }


    public Review addReview(Review review) {
        return reviewsStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewsStorage.updateReviews(review);
    }

    public String deleteReviewById(int id) {
        return deleteReviewById(id);
    }

    public Review getReviewById(int id) {
        return getReviewById(id);
    }

    public List<Review> getListReviewsByIdWithLimit(int filmId, int count) {
        return reviewsStorage.getListReviewsByIdWithLimit(filmId, count);
    }
}
