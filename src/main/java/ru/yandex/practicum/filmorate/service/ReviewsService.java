package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EmptyReviewsListException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.List;

@Service
public class ReviewsService {

    private final ReviewsStorage reviewsStorage;
    private final UserService userService;
    private final FilmService filmService;

    public ReviewsService(ReviewsStorage reviewsStorage, UserService userService, FilmService filmService) {
        this.reviewsStorage = reviewsStorage;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Review addReview(Review review) {
        if (validateReview(review)
                && userService.getUserById(review.getUserId()) != null
                && filmService.getFilmById(review.getFilmId()) != null) {
            if (!reviewsStorage.checkReviewInDb(review)) {
                return reviewsStorage.addReview(review);
            } else {
                throw new NotFoundException("Отзыв не найден");
            }
        } else {
            throw new ValidationException("Такой отзыв уже существует");
        }
    }

    public List<Review> getAllReviews() {
        return reviewsStorage.getAllReviews();
    }

    public Review updateReview(Review review) {
        if (reviewsStorage.checkReviewInDb(review.getId())) {
            return reviewsStorage.updateReviews(review);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public String deleteReviewById(int id) {
        if (reviewsStorage.checkReviewInDb(id)) {
            return reviewsStorage.deleteReviewById(id);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public Review getReviewById(int id) {
        return reviewsStorage.getReviewById(id);
    }

    public List<Review> getListReviewsByFilmIdWithLimit(int filmId, int count) {
        List<Review> result = reviewsStorage.getListReviewsByFilmIdWithLimit(filmId, count);
        if (!result.isEmpty()) {
            return result;
        } else {
            throw new EmptyReviewsListException("Ошибка базы или неверный запрос");
        }
    }

    public String addUserLikeToReview(int reviewId, int userId) {
        if (reviewsStorage.checkReviewInDb(reviewId)) {
            return reviewsStorage.addLike(reviewId, userId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public String addUserDislikeToReview(int reviewId, int userId) {
        if (reviewsStorage.checkReviewInDb(reviewId)) {
            return reviewsStorage.addDislike(reviewId, userId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public String deleteUserLikeFromReview(int reviewId, int userId) {
        if (reviewsStorage.checkReviewInDb(reviewId)
                && reviewsStorage.isHaveUsersLikeOrDislike(userId)) {
            return reviewsStorage.deleteLike(reviewId, userId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public String deleteUserDislikeFromReview(int reviewId, int userId) {
        if (reviewsStorage.checkReviewInDb(reviewId)
                && reviewsStorage.isHaveUsersLikeOrDislike(userId)) {
            return reviewsStorage.deleteDislike(reviewId, userId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public List<Review> getReviewByFilmId(int filmId) {
        return reviewsStorage.getReviewByFilmId(filmId);
    }

    private boolean validateReview(Review review) {
        if (review.getFilmId() < 0 || review.getUserId() < 0) {
            throw new NotFoundException("Отрицательное значение Id");
        } else if (review.getFilmId() == null
                || review.getUserId() == null) {
            throw new ValidationException("Ошибка валидации");
        }
        return true;
    }


}