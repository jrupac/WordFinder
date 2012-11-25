package apps.jrupac.WordFinder.utils;

public class Utils {
    public static String TAG(Class<?> c) {
        return "WordFinderApp:" + c.getSimpleName();
    }

    public static String TAG(Object o) {
        return TAG(o.getClass());
    }
}
