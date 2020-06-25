import com.blade.Blade;

/**
 * @author darren
 * @date 2019/10/16 16:59
 */
public class PkgNpeTest {

    public static void main(String[] args) {
        Blade.of()
                .get("/*/uuu", ctx -> ctx.text("hhh"))
                .get("/:bbb/bbb", ctx -> ctx.text(ctx.pathString("bbb")))
                .start();
    }
}
