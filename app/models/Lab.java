package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class Lab extends Model {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "lab_seq")
    private Integer labId;

    private String labTitle;

    @Constraints.Required
    private Date labDeathLine;

    @Constraints.Required
    @ManyToOne
    private StudGroup labGroup;

    public Lab(String labTitle, Date labDeathLine, StudGroup labGroup) {
        this.labTitle = labTitle;
        this.labDeathLine = labDeathLine;
        this.labGroup = labGroup;
    }

    public ObjectNode getLabInfo() {
        ObjectNode getLabInfo = Json.newObject();
        DateFormat date = new SimpleDateFormat("dd.MM.yy");
        getLabInfo.put("labId", this.labId);
        getLabInfo.put("labTitle", this.labTitle);
        getLabInfo.put("labDeathLine", date.format(labDeathLine));
        getLabInfo.put("labStudCount", doneCounter());
        return getLabInfo;
    }

    private String doneCounter () {
        List<DoneLab> doneLabsList = DoneLab.find.where().eq("donelabLab", this).findList();
        List<Student> studsList = Student.find.where().eq("studStudGroup", labGroup).findList();
        if (doneLabsList.size() < studsList.size()) {
            return Integer.toString(doneLabsList.size());
        } else {
            return "Все";
        }
    }

    public Integer getLabId() {
        return labId;
    }

    public void setLabId(Integer labId) {
        this.labId = labId;
    }

    public String getLabTitle() {
        return labTitle;
    }

    public void setLabTitle(String labTitle) {
        this.labTitle = labTitle;
    }

    public Date getLabDeathLine() {
        return labDeathLine;
    }

    public void setLabDeathLine(Date labDeathLine) {
        this.labDeathLine = labDeathLine;
    }

    public StudGroup getLabGroup() {
        return labGroup;
    }

    public void setLabGroup(StudGroup labGroup) {
        this.labGroup = labGroup;
    }

    public static Finder<String, Lab> find = new Finder<String, Lab>(String.class, Lab.class);
}
