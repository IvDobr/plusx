package models;


import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends Model {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy= GenerationType.SEQUENCE,
            generator="student_seq")
    private Integer studId;

    private String studFirstName;

    @Constraints.Required
    private String studLastName;

    @Constraints.Required
    @ManyToOne
    private StudGroup studStudGroup;

    public Student(String studLastName, String studFirstName, StudGroup studStudGroup) {
        this.studFirstName = studFirstName;
        this.studLastName = studLastName;
        this.studStudGroup = studStudGroup;
    }

    public ObjectNode getStudInfo() {
        ObjectNode getStudInfo = Json.newObject();
        List<Plus> plusList = Plus.find.where().eq("plusStud", this).findList();
        List<DoneLab> doneLabList = DoneLab.find.where().eq("donelabStud", this).findList();
        getStudInfo.put("studId", this.studId);
        getStudInfo.put("studFirstName", this.studFirstName);
        getStudInfo.put("studLastName", this.studLastName);
        getStudInfo.put("studPlusCount", plusList.size());
        getStudInfo.put("studLabCount", doneLabList.size());

        return getStudInfo;
    }

    public ObjectNode getPlusInfo() {
        ObjectNode getStudInfo = Json.newObject();
        List<Plus> plusList = Plus.find.where().eq("plusStud", this).findList();
        ArrayList<ObjectNode> plusInfo = new ArrayList<>();
        for(Plus plus: plusList) {
            plusInfo.add(plus.getPlusInfo());
        }

        getStudInfo.put("plusInfo", Json.toJson(plusInfo));

        return getStudInfo;
    }

    public ObjectNode getLabInfo() {
        ObjectNode getStudInfo = Json.newObject();
        List<DoneLab> labsList = DoneLab.find.where().eq("donelabStud", this).findList();
        ArrayList<ObjectNode> labInfo = new ArrayList<>();
        for(DoneLab doneLab: labsList) {
            labInfo.add(doneLab.getLabInfo());
        }

        getStudInfo.put("labInfo", Json.toJson(labInfo));

        return getStudInfo;
    }

    public Integer getStudId() {
        return studId;
    }

    public void setStudId(Integer studId) {
        this.studId = studId;
    }

    public String getStudFirstName() {
        return studFirstName;
    }

    public void setStudFirstName(String studFirstName) {
        this.studFirstName = studFirstName;
    }

    public String getStudLastName() {
        return studLastName;
    }

    public void setStudLastName(String studLastName) {
        this.studLastName = studLastName;
    }

    public StudGroup getStudStudGroup() {
        return studStudGroup;
    }

    public void setStudStudGroup(StudGroup studStudGroup) {
        this.studStudGroup = studStudGroup;
    }

    public static Finder<String, Student> find = new Finder<String, Student>(String.class, Student.class);
}

