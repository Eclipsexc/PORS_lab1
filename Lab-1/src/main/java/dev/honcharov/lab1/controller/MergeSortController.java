package dev.honcharov.lab1.controller;

import dev.honcharov.lab1.dto.MergeSortRequest;
import dev.honcharov.lab1.dto.MergeSortResponse;
import dev.honcharov.lab1.service.MediatorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merge-sort")
public class MergeSortController {

    private final MediatorService mediatorService;

    public MergeSortController(MediatorService mediatorService) {
        this.mediatorService = mediatorService;
    }

    @PostMapping
    public MergeSortResponse sort(@RequestBody @Valid MergeSortRequest request) {
        return mediatorService.execute(request);
    }
}