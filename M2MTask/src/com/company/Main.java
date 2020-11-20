package com.company;

import java.util.*;

public class Main {

    private static boolean isAResult = false;

    public static void findAllPermutations(int n, int[] rowIndexArray, int[][] matrix) {

        if (n == 1) {
            findNotDescendingDiagonal(rowIndexArray, matrix);
        } else {
            for (int i = 0; i < n - 1; i++) {
                findAllPermutations(n - 1, rowIndexArray, matrix);
                if (n % 2 == 0) {
                    swap(rowIndexArray, i, n - 1);
                } else {
                    swap(rowIndexArray, 0, n - 1);
                }
            }
            findAllPermutations(n - 1, rowIndexArray, matrix);
        }
    }

    private static void findNotDescendingDiagonal(int[] rowIndexArray, int[][] matrix) {

        int N = matrix.length;
        List<Integer> diagonal = new ArrayList<>();
        List<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < rowIndexArray.length; i++) {
            diagonal.add(matrix[rowIndexArray[i]][i]);
        }
        tempList.addAll(diagonal);
        Collections.sort(tempList);

        if (diagonal.equals(tempList)) {
            isAResult = true;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    System.out.print(matrix[rowIndexArray[i]][j] + " ");
                }
                System.out.println();
            }
            System.out.println("=====");
        }
    }

    private static void swap(int[] rowIndexArray, int a, int b) {
        int tmp = rowIndexArray[a];
        rowIndexArray[a] = rowIndexArray[b];
        rowIndexArray[b] = tmp;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter matrix dimensions: ");
        int N = input.nextInt();
        System.out.println("Input matrix: ");
        int matrix[][] = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = input.nextInt();
            }
        }
        int rowIndexArray[] = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            rowIndexArray[i] = i;
        }
        System.out.println();
        findAllPermutations(matrix.length, rowIndexArray, matrix);
        if (!isAResult) {
            System.out.println("NO_RESULT");
        }

    }
}