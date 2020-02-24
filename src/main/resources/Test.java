public class Test {
    public static void staticMethod(String param) {}
    public void normalMethod(String... param) {}

    public static class StaticInner {
        public static void staticMethod(String param) {}
        public void normalMethod(String... param) {}
    }

    public class Inner {
        public static void staticMethod(String param) {}
        public void normalMethod(String... param) {}

        public static class InnerInner {
            public static void staticMethod(String param) {}
            public void normalMethod(String... param) {}
        }
    }
}