import model.Player;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.RequestParams;
import net.formio.servlet.ServletRequestParams;
import net.formio.validation.ValidationResult;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

public class start {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    public static void main(String[] args) {

        final VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

        port(4444);

        staticFileLocation("/public");

        // match
        path("/match", () -> {
            before("/*", (q, a) -> System.out.println("SOME STUFF"));
            post("", (req, res) -> {
                return "create new match";
            });
            get("/:id",  (req, res) -> {
                return req.params();
            });
            put("/:status", (req, res) -> {
                final String command = req.params(":status");
                return command;
            });
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });

        get("/test", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "public/bootstrap/docs/examples/starter-template/index.html");
        }, velocityTemplateEngine);
        get("/player", (req, res) -> {

            Player player = new Player();
            player.setForename("Aleks");
            player.setEmail("Aleksandar.Gjurcinov@bechtle.com");
            player.setSurname("Gj");
            FormData<Player> formData = new FormData<>(player, ValidationResult.empty);

            FormMapping<Player> filledForm = playerForm.fill(formData);


            final HashMap<String, FormMapping<Player>> stringPlayerHashMap = new HashMap<>();
            stringPlayerHashMap.put("playerForm", filledForm);

            return new ModelAndView(stringPlayerHashMap, "views/player.vm");
        }, velocityTemplateEngine);

        post("/player", (req, res) -> {

            final HttpServletRequest raw = req.raw();
            final Map<String, String[]> parameterMap = raw.getParameterMap();

            RequestParams params = new ServletRequestParams(req.raw());
            FormData<Player> formData = playerForm.bind(params);


            FormMapping<Player> filledForm = filledForm = playerForm.fill(formData);;
            if (formData.isValid()) {
                Player player = formData.getData();
                System.out.println(player.getForename());
                System.out.println(player.getSurname());


            }
            else {
                final ValidationResult validationResult = formData.getValidationResult();
                System.out.println(validationResult);
            }

            final HashMap<String, FormMapping<Player>> stringPlayerHashMap = new HashMap<>();
            stringPlayerHashMap.put("playerForm", filledForm);
            return new ModelAndView(stringPlayerHashMap, "views/player.vm");
        }, velocityTemplateEngine);

    }


}
