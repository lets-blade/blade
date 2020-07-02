package benchmark;

import com.blade.mvc.route.DynamicMapping;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.mapping.dynamic.RegexMapping;
import com.blade.mvc.route.mapping.dynamic.TrieMapping;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author: dqyuan
 * @date: 2020/06/26
 */
@BenchmarkOptions(warmupRounds = 10, benchmarkRounds = 1000)
public class UrlRouterBenchMark {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    private static TrieMapping trieMapping = new TrieMapping();
    private static RegexMapping regexMapping = new RegexMapping();

    @BeforeClass
    public static void prepare() throws Exception {
        Path routes = Paths.get(UrlRouterBenchMark.class.getResource("routes").toURI());
        RoutesReader.initMapping(routes, (httpMethod, path, variables) -> {
            String originalPath = path;

            // [/** | /*]
            path = "*".equals(path) ? "/.*" : path;
            path = path.replace("/**", "/.*").replace("/*", "/.*");
            Route route = new Route(httpMethod,
                    originalPath, path, null, null, null);
            trieMapping.addRoute(httpMethod, route, variables);
            regexMapping.addRoute(httpMethod, route, variables);
        });

        trieMapping.register();
        regexMapping.register();
    }

    @Test
    public void regexInvokeBadUrl() throws URISyntaxException, IOException {
        runTest("GET", regexMapping, "/badUrl/whatever/sefesg/abc/136");
    }

    @Test
    public void trieInvokeBadUrl() {
        runTest("GET", trieMapping, "/badUrl/whatever/sefesg/abc/136");
    }


    @Test
    public void regexInvokeWildUrl() throws URISyntaxException, IOException, InterruptedException {
        runTest("POST", regexMapping, "/shuju/yemian/sgsgrsrhshhsh/component/shrrrshr444457");
    }

    @Test
    public void trieInvokeWildUrl() throws URISyntaxException, IOException {
        runTest("POST", trieMapping, "/shuju/yemian/sgsgrsrhshhsh/component/shrrrshr444457");
    }


    private void runTest(String method, DynamicMapping dynamicMapping,
                         String url) {
        final int loop = 10000;
        for (int i = 0; i < loop; i++) {
            Route route = dynamicMapping.findRoute(method, url);
            // route to black hole, prevent route from being optimized
            if (route != null && "4243t".equals(route.getPath())) {
                throw new RuntimeException();
            }
        }
    }

}
