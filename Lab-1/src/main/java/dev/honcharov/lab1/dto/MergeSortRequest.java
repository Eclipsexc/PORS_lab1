package dev.honcharov.lab1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MergeSortRequest (

        @NotNull(message = "implementation can't be null")
        Implementation implementation,

        @Min(value = 1, message = "arrayLengthToGenerate can't be smaller than 1")
        @Max(value = 100_000_000, message = "arrayLengthToGenerate can't be bigger than 100_000_000")
        Integer arrayLengthToGenerate,

        String sourceFilePath,

        @NotNull(message = "includeSortedArrayInResponse can't be null")
        boolean includeSortedArrayInResponse,

        @NotNull(message = "fileName can't be null")
        @NotBlank(message = "fileName can't be blank")
        String fileName
) {}
