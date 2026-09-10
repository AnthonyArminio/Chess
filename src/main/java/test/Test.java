package test;

public class Test {
    public static void main(String[] args) {
        float a = Float.POSITIVE_INFINITY;
        float b = Float.POSITIVE_INFINITY;

        System.out.println(a < b);
        System.out.println(b < a);
        System.out.println(a == b);

    }
}
