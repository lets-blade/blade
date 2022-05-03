package netty_hello;

import com.hellokaton.blade.ioc.annotation.Bean;
import com.hellokaton.blade.ioc.annotation.Inject;

/**
 * @author biezhi
 * @date 2018-11-21
 */
@Bean
public class UserService {

    @Inject
    private BService bService;

    @Inject
    private CService cService;

    public void sayHello(){
        bService.sayHello();
        cService.sayHello();
    }

}
