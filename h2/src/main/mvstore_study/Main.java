package mvstore_study;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.value.VersionedValue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author pfjia
 * @since 2019/4/22 15:28
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException {
        System.out.println(getDeclaredMethod(VersionedValue.class));


    }

    public static String stacktraceToUml(int ss, int j) throws NoSuchMethodException {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder result = new StringBuilder();
        for (int i = stackTraceElements.length - 1; i >= 0; i--) {
            StackTraceElement now = stackTraceElements[i];
            System.out.println(now.getClassName());
            String[] nowClassNameSplit = now.getClassName().split("\\.");
            String nowClassName = nowClassNameSplit[nowClassNameSplit.length - 1 < 0 ? 0 : nowClassNameSplit.length - 1];
            nowClassName = nowClassName.replace("$", "_");

//            去除this方法所占的stacktrack

            if (Objects.equals(now.getClassName(), "java.lang.Thread") && Objects.equals(now.getMethodName(), "getStackTrace")) {
                continue;
            }
            Method method = Main.class.getMethod("stacktraceToUml");
            if (Objects.equals(now.getClassName(), Main.class.getCanonicalName()) && Objects.equals(now.getMethodName(), method.getName())) {
                continue;
            }


            if (i == stackTraceElements.length - 1) {
                result.append("[->")
                        .append(nowClassName)
                        .append(":")
                        .append(now.getMethodName());
            } else {
                StackTraceElement prev = stackTraceElements[i + 1];
                String[] prevClassNameSplit = prev.getClassName().split("\\.");
                String prevClassName = prevClassNameSplit[prevClassNameSplit.length - 1 < 0 ? 0 : prevClassNameSplit.length - 1];
                prevClassName = prevClassName.replace("$", "_");
                result.append(prevClassName)
                        .append("->")
                        .append(nowClassName)
                        .append(":")
                        .append(now.getMethodName());
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static String getDeclaredMethod(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        return Arrays.stream(methods)
                .map(new Function<Method, String>() {
                    @Override
                    public String apply(Method method) {
                        StringBuilder sb = new StringBuilder();
                        int modifiers = method.getModifiers();
                        sb.append(Modifier.isPublic(modifiers) ? "+ " : "")
                                .append(Modifier.isStatic(modifiers) ? "static " : "")
                                .append(method.getReturnType().getSimpleName())
                                .append(" ")
                                .append(method.getName())
                                .append("(");
                        Parameter[] parameters = method.getParameters();
                        for (int i = 0; i < parameters.length; i++) {
                            Parameter parameter = parameters[i];
                            sb.append(parameter.getType().getSimpleName())
                                    .append(" ")
                                    .append(parameter.getName());
                            if (i != parameters.length - 1) {
                                sb.append(",");
                            }
                        }
                        sb.append(")");
                        return sb.toString();
                    }
                }).reduce((s, s2) -> s + "\n" + s2).orElse("");
    }

    public static void foo() throws IOException {
        String fileName = "C:\\Users\\pfjia\\Desktop\\test";
        Files.delete(Path.of(fileName));
        MVStore s = MVStore.open(fileName);
        // create/get the map named "data"
        MVMap<Integer, String> map = s.openMap("data");

        for (int i = 0; i < 100; i++) {
            // add and read some data
            map.put(i, "Hello World" + i);
        }
        System.out.println(map.get(1));

        // close the store (this will persist changes)
        s.close();
    }
}
