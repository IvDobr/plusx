package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Plus extends Model {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "plus_seq")
    private Integer plusId;

    @Constraints.Required
    private Date plusDate;

    @Constraints.Required
    @ManyToOne
    private Student plusStud;

    public Plus(Student plusStud) {
        this.plusStud = plusStud;
        this.plusDate = new java.util.Date();
    }

    public ObjectNode getPlusInfo() {
        ObjectNode getPlusInfo = Json.newObject();
        DateFormat date = new SimpleDateFormat("dd.MM.yy HH:mm");
        getPlusInfo.put("plusId", this.plusId);
        getPlusInfo.put("plusDate", date.format(plusDate));
        return getPlusInfo;
    }

    public Integer getPlusId() {
        return plusId;
    }

    public void setPlusId(Integer plusId) {
        this.plusId = plusId;
    }

    public Date getPlusDate() {
        return plusDate;
    }

    public void setPlusDate(Date plusDate) {
        this.plusDate = plusDate;
    }

    public Student getPlusStud() {
        return plusStud;
    }

    public void setPlusStud(Student plusStud) {
        this.plusStud = plusStud;
    }

    public static Finder<String, Plus> find = new Finder<String, Plus>(String.class, Plus.class);
}
