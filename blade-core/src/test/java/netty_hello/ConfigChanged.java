package netty_hello;

import com.hellokaton.blade.Environment;
import com.hellokaton.blade.event.Event;
import com.hellokaton.blade.event.EventListener;
import com.hellokaton.blade.ioc.annotation.Bean;

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
