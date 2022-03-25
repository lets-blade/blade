package netty_hello;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author chenjiang
 * @Date 2022/3/25/025 9:56
 */
@Getter
@Setter
public class MapValues {

    ChannelFuture writeFuture;
    ChannelPromise promise;

    String response;

    public MapValues(ChannelFuture writeFuture, ChannelPromise promise) {
        this.writeFuture=writeFuture;
        this.promise=promise;
    }
}
