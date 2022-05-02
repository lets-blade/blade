package netty_hello;

import com.blade.ioc.annotation.Bean;

/**
 * @author biezhi
 * @date 2018-11-21
 */
@Bean
public class BService {

    public void sayHello(){
        System.out.println("B...Hello");
    }

}
