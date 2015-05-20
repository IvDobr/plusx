package controllers;

import models.Prepod;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.auth;

import java.util.List;

public class Secure extends Controller {

    public static Result index() {
        if(session("current_user") == null){
            System.out.println("INFO: Незалогинен");
            return ok(auth.render(""));
        } else {
            System.out.println("INFO: Залогинен");
            return redirect(controllers.routes.Application.index());
        }
    }

    public static Result logInProc() {
        DynamicForm requestData = Form.form().bindFromRequest();

        List<Prepod> prepodList = Prepod.find.where()
                .ilike("prepodLogin", requestData.get("prepodLogin"))
                .findList();

        Prepod current_user;
        try{
            current_user = prepodList.get(0);
        } catch (Exception e){
            System.out.println("INFO: Неверный логин или пароль!");
            return badRequest(auth.render("Неверный логин или пароль!"));
        }

        if (current_user != null) {
            if (requestData.get("prepodPass").equals(current_user.getPrepodPass())) {
                session("current_user", Crypto.encryptAES(current_user.getPrepodId().toString()));
                return redirect(controllers.routes.Application.index());
            } else {
                System.out.println("INFO: Неверный логин или пароль!");
                return badRequest(auth.render("Неверный логин или пароль!"));
            }
        } else {
            System.out.println("INFO: Неверный логин или пароль!");
            return badRequest(auth.render("Неверный логин или пароль!"));
        }
    }

    public static Result logOutProc() {
        session().clear();
        System.out.println("INFO: Выход из сессии");
        return redirect(controllers.routes.Secure.index());
    }
}