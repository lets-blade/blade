package benchmark;

import com.blade.kit.StringKit;
import com.blade.mvc.http.HttpMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: dqyuan
 * @date: 2020/06/26
 */
public class RoutesReader {

    public static void initMapping(Path routeFilePath, TripleConsumer initer) throws IOException {
        List<String> lines = Files.readAllLines(routeFilePath);
        lines.forEach(line -> {
            String trimedLine = line.trim();
            if (StringKit.isBlank(trimedLine) || trimedLine.startsWith("#")) {
                return;
            }

            String[] methodAndRoute = trimedLine.split(" ");
            if (methodAndRoute.length < 2) {
                throw new RuntimeException("one line must be composed of method, route and [variables]");
            }
            String method = methodAndRoute[0];
            String route = methodAndRoute[1];
            List<String> variables = new ArrayList<>();
            if (methodAndRoute.length >= 3 && StringKit.isNotBlank(methodAndRoute[2])) {
                variables.addAll(
                        Arrays.stream(methodAndRoute[2].split(","))
                                .collect(Collectors.toList())
                );

            }
            HttpMethod httpMethod = "*".equals(method)? HttpMethod.ALL:
                    HttpMethod.valueOf(method);

            initer.accept(httpMethod, route, variables);
        });
    }

}
