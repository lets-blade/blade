package netty_hello;

import com.blade.Environment;
import com.blade.event.Event;
import com.blade.event.EventListener;
import com.blade.ioc.annotation.Bean;

/**
 * @author biezhi
 * @date 2017/12/24
 */
@Bean
public class ConfigChanged implements EventListener<Environment> {

    @Override
    public void trigger(Event<Environment> e) {
        Environment environment = e.data();
        System.out.println("EventListener::ConfigChanged");
        System.out.println(environment.toMap());
    }

}
