package dev.honcharov.lab1.dto;

public record MergeSortResponse(
        String sortedArray,
        long durationInMs
) {
}
