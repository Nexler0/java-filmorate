package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @PostMapping
    public Review addReview(Review review){
        return reviewsService.addReview(review);
    }

    @PutMapping
    public Review updateReview(Review review){
        return reviewsService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public String deleteReviewById(@PathVariable int id){
        return reviewsService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id){
        return reviewsService.getReviewById(id);
    }

    @GetMapping("?filmId={filmId}&count={count}")
    public List<Review> getListReviewsByIdWithLimit(@PathVariable int filmId, @PathVariable int count){
        return reviewsService.getListReviewsByIdWithLimit(filmId, count);
    }
}
