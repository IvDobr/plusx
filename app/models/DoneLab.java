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
public class DoneLab extends Model {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "donelab_seq")
    private Integer donelabId;

    @Constraints.Required
    private Date donelabDate;

    @Constraints.Required
    @ManyToOne
    private Student donelabStud;

    @Constraints.Required
    @ManyToOne
    private Lab donelabLab;

    public DoneLab(Date donelabDate, Student donelabStud, Lab donelabLab) {
        this.donelabDate = donelabDate;
        this.donelabStud = donelabStud;
        this.donelabLab = donelabLab;
    }

    public ObjectNode getLabInfo() {
        ObjectNode getLabInfo = Json.newObject();
        DateFormat date = new SimpleDateFormat("dd.MM.yy HH:mm");
        getLabInfo.put("doneLabId", this.donelabId);
        getLabInfo.put("doneLabDate", date.format(donelabDate));
        getLabInfo.put("doneLabLab", this.donelabLab.getLabTitle());
        return getLabInfo;
    }

    public Integer getDonelabId() {
        return donelabId;
    }

    public void setDonelabId(Integer donelabId) {
        this.donelabId = donelabId;
    }

    public Date getDonelabDate() {
        return donelabDate;
    }

    public void setDonelabDate(Date donelabDate) {
        this.donelabDate = donelabDate;
    }

    public Student getDonelabStud() {
        return donelabStud;
    }

    public void setDonelabStud(Student donelabStud) {
        this.donelabStud = donelabStud;
    }

    public Lab getDonelabLab() {
        return donelabLab;
    }

    public void setDonelabLab(Lab donelabLab) {
        this.donelabLab = donelabLab;
    }

    public static Finder<String, DoneLab> find = new Finder<String, DoneLab>(String.class, DoneLab.class);
}
