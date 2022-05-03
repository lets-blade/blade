package benchmark;

import com.hellokaton.blade.mvc.http.HttpMethod;

import java.util.List;

/**
 * @author: dqyuan
 * @date: 2020/06/26
 */
public interface TripleConsumer {

    void accept(HttpMethod httpMethod, String path, List<String> uriVariables);

}
