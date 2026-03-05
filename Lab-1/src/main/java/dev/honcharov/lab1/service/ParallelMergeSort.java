package dev.honcharov.lab1.service;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort implements MergeSort {

    private final ForkJoinPool pool;
    private final int cores;

    public ParallelMergeSort(ForkJoinPool pool, int cores) {
        this.pool = pool;
        this.cores = cores;
    }

    @Override
    public void mergeSort(int[] arr, int left, int right) {
        int totalElements = right - left + 1;
        int threshold = Math.max(totalElements / cores, 1);
        pool.invoke(new MergeSortTask(arr, left, right, threshold));
    }

    private class MergeSortTask extends RecursiveAction {

        private final int[] arr;
        private final int left;
        private final int right;
        private final int threshold;

        MergeSortTask(int[] arr, int left, int right, int threshold) {
            this.arr = arr;
            this.left = left;
            this.right = right;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (left >= right) return;

            if ((right - left + 1) <= threshold) {
                sequentialMergeSort(arr, left, right);
                return;
            }

            int mid = (left + right) / 2;

            invokeAll(
                    new MergeSortTask(arr, left, mid, threshold),
                    new MergeSortTask(arr, mid + 1, right, threshold)
            );

            merge(arr, left, mid, right);
        }

        private void sequentialMergeSort(int[] arr, int left, int right) {
            if (left >= right) return;
            int mid = (left + right) / 2;
            sequentialMergeSort(arr, left, mid);
            sequentialMergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }
}