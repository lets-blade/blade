import com.blade.Blade;

/**
 * @author darren
 * @date 2019/10/16 16:59
 */
public class PkgNpeTest {

    public static void main(String[] args) {
        Blade.of()
                .get("/aaa/*/ccc", ctx -> ctx.text("popopo"))
                .get("/cde/uuu", ctx -> ctx.text("efef"))
                .get("/mmm/:id/:name", ctx -> ctx.text(ctx.pathString("id") +
                        "-" + ctx.pathString("name")))
                .post("/bbb/:id/:name", ctx -> ctx.text(ctx.pathString("id") +
                        ":" + ctx.pathString("name")))
                .start();
    }
}
