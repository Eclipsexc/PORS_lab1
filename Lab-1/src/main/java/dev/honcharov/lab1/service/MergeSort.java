package dev.honcharov.lab1.service;

public interface MergeSort {

    void mergeSort(int[] arr, int left, int right);

    default void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        System.arraycopy(arr, left, leftArr, 0, n1);
        System.arraycopy(arr, mid + 1, rightArr, 0, n2);

        int i = 0;
        int j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (leftArr[i] <= rightArr[j]) {
                arr[k] = leftArr[i];
                i++;
            } else {
                arr[k] = rightArr[j];
                j++;
            }
            k++;
        }

        for (; i < n1; k++, i++) {
            arr[k] = leftArr[i];
        }
        for (; j < n2; k++, j++) {
            arr[k] = rightArr[j];
        }
    }
}
