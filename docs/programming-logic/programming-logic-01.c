#include <stdio.h>

// Prototype of Functions
void mergeSort(int *, int, int);
void merge(int *, int, int, int);
int removeDuplicates(int *, int);
void printArray(int *, int);

// Main Code
int main(void) {
    int arr[] = {4, 2, 7, 2, 4, 9, 1};
    int size = sizeof(arr) / sizeof(arr[0]);

    printf("Original:\n");
    printArray(arr, size);

    mergeSort(arr, 0, size - 1);

    size = removeDuplicates(arr, size);

    printf("\nOrdenado sin duplicados:\n");
    printArray(arr, size);

    return 0;
}

// Functions
void mergeSort(int *arr, int p, int r) {
    if (p >= r)
        return;

    int q = (p + r) / 2;

    mergeSort(arr, p, q);
    mergeSort(arr, q + 1, r);
    merge(arr, p, q, r);
}

void merge(int *arr, int p, int q, int r) {
    int leftSize = q - p + 1;
    int rightSize = r - q;

    int left[leftSize];
    int right[rightSize];

    for (int i = 0; i < leftSize; i++)
        left[i] = arr[p + i];

    for (int i = 0; i < rightSize; i++)
        right[i] = arr[q + 1 + i];

    int i = 0, j = 0, k = p;

    while (i < leftSize && j < rightSize) {
        if (left[i] <= right[j])
            arr[k++] = left[i++];
        else
            arr[k++] = right[j++];
    }

    while (i < leftSize)
        arr[k++] = left[i++];

    while (j < rightSize)
        arr[k++] = right[j++];
}

int removeDuplicates(int *arr, int size) {
    if (size == 0)
        return 0;

    int j = 0;

    for (int i = 1; i < size; i++) {
        if (arr[i] != arr[j]) {
            j++;
            arr[j] = arr[i];
        }
    }

    return j + 1;
}

void printArray(int *arr, int size) {
    for (int i = 0; i < size; i++)
        printf("%d ", arr[i]);

    printf("\n");
}