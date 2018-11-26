package netty_hello;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Inject;

/**
 * @author biezhi
 * @date 2018-11-21
 */
@Bean(singleton = false)
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
