package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class StudGroup extends Model {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy= GenerationType.SEQUENCE,
            generator="studgroup_seq")
    private Integer groupId;

    private String groupTitle;

    @Constraints.Required
    @ManyToOne
    private Prepod gpoupPrepod;

    public StudGroup(String groupTitle, Prepod gpoupPrepod) {
        this.groupTitle = groupTitle;
        this.gpoupPrepod = gpoupPrepod;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public Prepod getGpoupPrepod() {
        return gpoupPrepod;
    }

    public void setGpoupPrepod(Prepod gpoupPrepod) {
        this.gpoupPrepod = gpoupPrepod;
    }

    public static Finder<String, StudGroup> find = new Finder<String, StudGroup>(String.class, StudGroup.class);
}