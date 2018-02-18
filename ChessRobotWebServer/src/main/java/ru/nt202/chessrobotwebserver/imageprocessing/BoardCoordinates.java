package ru.nt202.chessrobotwebserver.imageprocessing;

class BoardCoordinates {

    private static int x0;
    private static int y0;
    private static int x1;
    private static int y1;
    private static int x2;
    private static int y2;
    private static int x3;
    private static int y3;

//    0 -> left, up; (h1)
//    1 -> right, up; (a1)
//    2 -> right, down; (a8)
//    3 -> left, down; (h8)

    BoardCoordinates(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3) {
        BoardCoordinates.x0 = x0;
        BoardCoordinates.y0 = y0;
        BoardCoordinates.x1 = x1;
        BoardCoordinates.y1 = y1;
        BoardCoordinates.x2 = x2;
        BoardCoordinates.y2 = y2;
        BoardCoordinates.x3 = x3;
        BoardCoordinates.y3 = y3;
    }

    static int getX0() {
        return x0;
    }

    static int getY0() {
        return y0;
    }

    static int getX1() {
        return x1;
    }

    static int getY1() {
        return y1;
    }

    static int getX2() {
        return x2;
    }

    static int getY2() {
        return y2;
    }

    static int getX3() {
        return x3;
    }

    static int getY3() {
        return y3;
    }
}
