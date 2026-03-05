package dev.honcharov.lab1.service;

import dev.honcharov.lab1.dto.Implementation;
import dev.honcharov.lab1.dto.MergeSortRequest;
import dev.honcharov.lab1.dto.MergeSortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

@Service
public class MediatorService {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L;

    private final Random randomNumberGenerator;
    private final ParallelMergeSort parallelMergeSort;
    private final SequentialMergeSort sequentialMergeSort;
    private final ObjectMapper objectMapper;

    @Value("${app.save-dir:saved-requests}")
    private String saveDir;

    public MediatorService(Random randomNumberGenerator, ParallelMergeSort parallelMergeSort, SequentialMergeSort sequentialMergeSort, ObjectMapper objectMapper) {
        this.randomNumberGenerator = randomNumberGenerator;
        this.parallelMergeSort = parallelMergeSort;
        this.sequentialMergeSort = sequentialMergeSort;
        this.objectMapper = objectMapper;
    }

    public MergeSortResponse execute(MergeSortRequest request) {
        saveRequest(request);

        MergeSort mergeSort = request.implementation() == Implementation.PARALLEL
                ? parallelMergeSort
                : sequentialMergeSort;

        int[] array = resolveArray(request);

        long start = System.currentTimeMillis();
        mergeSort.mergeSort(array, 0, array.length - 1);
        long duration = System.currentTimeMillis() - start;

        String responseArray = request.includeSortedArrayInResponse()
                ? Arrays.toString(array)
                : null;

        return new MergeSortResponse(responseArray, duration);
    }

    private int[] resolveArray(MergeSortRequest request) {
        boolean hasFile = request.sourceFilePath() != null && !request.sourceFilePath().isBlank();
        boolean hasLength = request.arrayLengthToGenerate() != null;

        if (hasFile && hasLength) {
            throw new IllegalArgumentException("Provide either 'sourceFilePath' or 'arrayLengthToGenerate', not both.");
        }
        if (!hasFile && !hasLength) {
            throw new IllegalArgumentException("Either 'sourceFilePath' or 'arrayLengthToGenerate' must be provided.");
        }

        return hasFile
                ? readArrayFromFile(request.sourceFilePath())
                : generateArray(request.arrayLengthToGenerate());
    }

    private int[] readArrayFromFile(String filePath) {
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Source file not found: " + filePath);
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Source path is not a file: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("Source file is not readable: " + filePath);
        }

        String lower = path.getFileName().toString().toLowerCase();
        if (!lower.endsWith(".json")) {
            throw new IllegalArgumentException("Invalid file extension. Expected .json file: " + filePath);
        }

        try {
            long size = Files.size(path);
            if (size == 0) {
                throw new IllegalArgumentException("Source file is empty: " + filePath);
            }
            if (size > MAX_FILE_SIZE_BYTES) {
                throw new IllegalArgumentException("Source file is too large (" + size + " bytes): " + filePath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not inspect source file: " + filePath, e);
        }

        try {
            int[] array = objectMapper.readValue(path.toFile(), int[].class);

            if (array == null || array.length == 0) {
                throw new IllegalArgumentException("Source file contains an empty array: " + filePath);
            }

            return array;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid JSON file format. Expected JSON array of integers, e.g. [3, 1, 4, 7]. File: " + filePath
            );
        }
    }

    private int[] generateArray(int arrayLength) {
        int[] array = new int[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            array[i] = randomNumberGenerator.nextInt(Integer.MAX_VALUE);
        }
        return array;
    }

    private void saveRequest(MergeSortRequest request) {
        try {
            Path dir = Path.of(saveDir);
            Files.createDirectories(dir);

            String sanitized = request.fileName().replaceAll("[^\\w.\\-]", "_");
            String name = sanitized.endsWith(".json") ? sanitized : sanitized + ".json";
            Path file = dir.resolve(name);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), request);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save request to file", e);
        }
    }
}