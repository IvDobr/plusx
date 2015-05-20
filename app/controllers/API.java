package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Routes;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class API extends Controller {

    public static Result getGroupsJSON() {
        Prepod current_user = Prepod.find.byId(Crypto.decryptAES(session("current_user")));
        List<StudGroup> groupsList = StudGroup.find.where()
                .eq("gpoupPrepod", current_user)
                .findList();
        ObjectNode result = Json.newObject();
        JsonNode groupsListJson = Json.toJson(groupsList);
        result.put("status", "OK");
        result.put("groupsListJson", groupsListJson);
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result createGroupJSON() {
        Prepod current_user = Prepod.find.byId(Crypto.decryptAES(session("current_user")));
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup studGroup = new StudGroup(request.findPath("newGroupTitle").textValue(), current_user);
        try{
            studGroup.save();
            System.out.println("MSG: Сохранена группа " + request.findPath("newGroupTitle").textValue());
            result.put("status","OK");
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }

        String[] stus_names = request.findPath("newGroupStudents").textValue().split("; ");
        for (String stud : stus_names) {
            String[] name = stud.split(" ");
            Student newStudent = new Student(name[0].replace("_", " "), name[1].replace("_", " "), studGroup);
            try{
                newStudent.save();
                System.out.println("MSG: Сохранен студент");
                result.put("status", "OK");
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result deleteGroupJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());
        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        List<Student> studList = Student.find.where()
                .eq("studStudGroup", group)
                .findList();

        //Для каждого студента
        for(Student stud: studList) {

            //Удаляем плюсы студента
            List<Plus> plusList = Plus.find.where()
                    .eq("plusStud", stud)
                    .findList();
            for(Plus plus: plusList) {
                try{
                    plus.delete();
                } catch(Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    result.put("status", "error");
                    return badRequest(result);
                }
            }

            //Удаляем выполненные лабы студента
            List<DoneLab> doneLabsList = DoneLab.find.where()
                    .eq("donelabStud", stud)
                    .findList();
            for(DoneLab doneLab: doneLabsList) {
                try{
                    doneLab.delete();
                } catch(Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    result.put("status", "error");
                    return badRequest(result);
                }
            }

            //Удаляем студента
            try{
                stud.delete();
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        }

        //Удаляем лабораторные
        List<Lab> labsList = Lab.find.where()
                .eq("labGroup", group)
                .findList();
        for(Lab lab: labsList) {
            try{
                lab.delete();
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        }

        try{
            group.delete();
            System.out.println("MSG: Удалена группа");
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result renameGroupJSON() {
        Prepod current_user = Prepod.find.byId(Crypto.decryptAES(session("current_user")));
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());
        group.setGroupTitle(request.findPath("groupNewTitle").textValue());
        try{
            if (!group.getGpoupPrepod().equals(current_user)) {throw new IllegalAccessException("Access denied!");}
            group.update();
            System.out.println("MSG: Переименована группа " + request.findPath("groupNewTitle").textValue());
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addStudToGroupJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

//        String[] stus_names = request.findPath("studNames").textValue().split("; ");
//        for (String stud : stus_names) {
//            String[] name = stud.split(" ");
//            Student newStudent = new Student(name[0].replace("_", " "), name[1].replace("_", " "), group);
//            try{
//                newStudent.save();
//                System.out.println("MSG: Сохранен студент");
//                result.put("status", "OK");
//            } catch(Exception e) {
//                System.out.println("ERROR: " + e.getMessage());
//                result.put("status", "error");
//                return badRequest(result);
//            }
//        }

        Student newStudent = new Student(request.findPath("studLast").textValue(), request.findPath("studFirst").textValue(), group);
            try{
                newStudent.save();
                System.out.println("MSG: Сохранен студент");
                result.put("status", "OK");
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result loadStudentsJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        List<Student> studsList = Student.find.where()
                .eq("studStudGroup", group)
                .findList();

        ArrayList<ObjectNode> studsInfo = new ArrayList<>();
        for(Student stud: studsList) {
            studsInfo.add(stud.getStudInfo());
        }

        result.put("status", "OK");
        result.put("studsListJson", Json.toJson(studsInfo));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getLabsJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        List<Lab> labsList = Lab.find.where()
                .eq("labGroup", group)
                .findList();

        ArrayList<ObjectNode> labsInfo = new ArrayList<>();
        for(Lab lab: labsList) {
            labsInfo.add(lab.getLabInfo());
        }

        result.put("status", "OK");
        result.put("labsListJson", Json.toJson(labsInfo));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result deleteLabJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());
        Lab lab = Lab.find.byId(request.findPath("labId").toString());
        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        List<DoneLab> doneLabsList = DoneLab.find.where()
                .eq("donelabLab", lab)
                .findList();
        for(DoneLab doneLab: doneLabsList) {
            try{
                doneLab.delete();
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        }

        try{
            lab.delete();
            System.out.println("MSG: Удалена лабораторная");
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result createLabJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        Lab newLab = new Lab(request.findPath("newLabTitle").textValue(), JsonToDate(request.findPath("newLabDeathLine").textValue()), group);
        try {
            newLab.save();
            System.out.println("MSG: Сохранена лабораторная");
            result.put("status", "OK");
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addPlusJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        Student student = Student.find.byId(request.findPath("studId").toString());

        Plus newPlus = new Plus(student);
        try{
            newPlus.save();
            System.out.println("MSG: Сохранен плюс");
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result doneLabJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        Student student = Student.find.byId(request.findPath("studId").toString());

        Lab lab = Lab.find.byId(request.findPath("labId").toString());

        List<DoneLab> dlList = DoneLab.find.where().eq("donelabStud", student).findList();

        for(DoneLab dl : dlList) {
            if (dl.getDonelabLab().equals(lab)) {return badRequest("Эта лаботарная этим студентом уже сдана");}
        }


        DoneLab done = new DoneLab(new java.util.Date(), student, lab);
        try{
            done.save();
            System.out.println("MSG: Сохранена сданная лаба");
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result loadPlusxJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        Student stud = Student.find.byId(request.findPath("studId").toString());

        result.put("status", "OK");
        result.putAll(stud.getPlusInfo());
        result.putAll(stud.getLabInfo());
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result editStudNameJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());

        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        Student stud = Student.find.byId(request.findPath("studId").toString());

        stud.setStudFirstName(request.findPath("studFirstName").textValue());
        stud.setStudLastName(request.findPath("studLastName").textValue());

        try{
            stud.update();
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result delDoneLabJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());
        DoneLab lab = DoneLab.find.byId(request.findPath("labId").toString());
        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        try{
            lab.delete();
            System.out.println("MSG: Удалена сданная лабораторная");
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result delPlusJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());
        Plus plus = Plus.find.byId(request.findPath("plusId").toString());
        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        try{
            plus.delete();
            System.out.println("MSG: Удален плюс");
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result deleteStudentJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        StudGroup group = StudGroup.find.byId(request.findPath("groupId").toString());
        if (!checkGroupOwner(group)) return badRequest("Access denied!");

        Student student = Student.find.byId(request.findPath("studId").toString());

        //Удаляем плюсы студента
        List<Plus> plusList = Plus.find.where()
                .eq("plusStud", student)
                .findList();
        for(Plus plus: plusList) {
            try{
                plus.delete();
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        }

        //Удаляем выполненные лабы студента
        List<DoneLab> doneLabsList = DoneLab.find.where()
                .eq("donelabStud", student)
                .findList();
        for(DoneLab doneLab: doneLabsList) {
            try{
                doneLab.delete();
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                result.put("status", "error");
                return badRequest(result);
            }
        }

        //Удаляем студента

        try{
            student.delete();
            System.out.println("MSG: Удален студент");
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    private static Boolean checkGroupOwner (StudGroup group) {
        Prepod current_user = Prepod.find.byId(Crypto.decryptAES(session("current_user")));
        try {
            if (!group.getGpoupPrepod().equals(current_user)) {
                throw new IllegalAccessException("Access denied!");
            }
        }
        catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static Date JsonToDate (String jsonDate) {
        SimpleDateFormat formatDateJSON = new SimpleDateFormat();
        formatDateJSON.applyPattern("yyyy-MM-dd");
        try {
            return formatDateJSON.parse(jsonDate);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            return new java.util.Date();
        }
    }

//    public static boolean checkWithRegExp(String str){
//        Pattern p = Pattern.compile("(([A-Za-Z]+(_[A-Za-Z]+)* [A-Za-Z]+(_[A-Za-Z]+)*)(; )?)+$");
//        Matcher m = p.matcher(str);
//        return m.matches();
//    }

//    private static ArrayList<Student> parseNames(String str) {
//        ArrayList<Student> parseStudents = new ArrayList<>();
//
//
//
//        String[] stus_names = str.split("; ");
//        for (String stud : stus_names) {
//            String[] name = stud.split(" ");
//            Student newStudent = new Student(name[0], name[1], studGroup);
//        }
//        return parseStudents;
//    }

    public static Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.API.getGroupsJSON(),
                        controllers.routes.javascript.API.createGroupJSON(),
                        controllers.routes.javascript.API.deleteGroupJSON(),
                        controllers.routes.javascript.API.renameGroupJSON(),
                        controllers.routes.javascript.API.addStudToGroupJSON(),
                        controllers.routes.javascript.API.loadStudentsJSON(),
                        controllers.routes.javascript.API.getLabsJSON(),
                        controllers.routes.javascript.API.deleteLabJSON(),
                        controllers.routes.javascript.API.createLabJSON(),
                        controllers.routes.javascript.API.addPlusJSON(),
                        controllers.routes.javascript.API.doneLabJSON(),
                        controllers.routes.javascript.API.loadPlusxJSON(),
                        controllers.routes.javascript.API.editStudNameJSON(),
                        controllers.routes.javascript.API.delDoneLabJSON(),
                        controllers.routes.javascript.API.delPlusJSON(),
                        controllers.routes.javascript.API.deleteStudentJSON()
                )
        );
    }
}
