package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping
public class ReviewsController {

    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @PostMapping("/reviews")
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewsService.addReview(review);
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewsService.getAllReviews();
    }

    @PutMapping("/reviews")
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewsService.updateReview(review);
    }

    @DeleteMapping("/reviews/{id}")
    public String deleteReviewById(@PathVariable int id) {
        return reviewsService.deleteReviewById(id);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewsService.getReviewById(id);
    }

    @GetMapping("/reviews")
    @ResponseBody
    public List<Review> getListReviewsWithParams(@RequestParam(required = false, name = "filmId") Integer filmId,
                                                 @RequestParam(required = false, name = "count") Integer count) {

        if (filmId != null && count != null) {
            return reviewsService.getListReviewsByFilmIdWithLimit(filmId, count);
        } else if (filmId != null) {
            return reviewsService.getReviewByFilmId(filmId);
        } else {
            return reviewsService.getAllReviews();
        }
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public String addUserLikeToReview(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.addUserLikeToReview(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public String addUserDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.addUserDislikeToReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public String deleteUserLikeToReview(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.deleteUserLikeFromReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public String deleteUserDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.deleteUserDislikeFromReview(id, userId);
    }
}
