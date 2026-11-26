package {{package}};

public class App {

    public static String greeting() {
        return "hello from {{artifact}}";
    }

    public static void main(String[] args) {
        System.out.println(greeting());
    }
}