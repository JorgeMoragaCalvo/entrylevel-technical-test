#include <stdio.h>

// Prototype of Functions
void merge_sort(int *, int, int);
void merge(int *, int, int, int);
int remove_duplicates(int *, int);
void print_array(int *, int);

// Main Code
int main(void) {
    int arr[] = {4, 2, 7, 2, 4, 9, 1};
    int size = sizeof(arr) / sizeof(arr[0]);

    printf("Original:\n");
    print_array(arr, size);

    merge_sort(arr, 0, size - 1);

    size = remove_duplicates(arr, size);

    printf("\nOrdenado sin duplicados:\n");
    print_array(arr, size);

    return 0;
}

// Functions
void merge_sort(int *arr, int p, int r) {
    if (p >= r)
        return;

    int q = (p + r) / 2;

    merge_sort(arr, p, q);
    merge_sort(arr, q + 1, r);
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

int remove_duplicates(int *arr, int size) {
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

void print_array(int *arr, int size) {
    for (int i = 0; i < size; i++)
        printf("%d ", arr[i]);

    printf("\n");
}