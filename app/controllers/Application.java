package controllers;

import models.Prepod;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        Prepod current_user = Prepod.find.byId(Crypto.decryptAES(session("current_user")));
        return ok(index.render(current_user.getPrepodFirstName() + " " + current_user.getPrepodLastName()));
    }
}
