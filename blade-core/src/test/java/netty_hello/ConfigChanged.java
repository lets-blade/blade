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
public class ConfigChanged implements EventListener {

    @Override
    public void trigger(Event e) {
        Environment environment = (Environment) e.attribute("environment");
        System.out.println("EventListener::ConfigChanged");
        System.out.println(environment.toMap());
    }

}
