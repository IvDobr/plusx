package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class Prepod extends Model {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy= GenerationType.SEQUENCE,
            generator="prepod_seq")
    private Integer prepodId;

    @Constraints.Required
    @Column(unique=true)
    private String prepodLogin;

    private String prepodFirstName;

    @Constraints.Required
    private String prepodLastName;

    @Constraints.Required
    private String prepodPass;

    public Prepod() {
    }

    public Prepod(String prepodLogin, String prepodFirstName, String prepodLastName, String prepodPass) {
        this.prepodLogin = prepodLogin;
        this.prepodFirstName = prepodFirstName;
        this.prepodLastName = prepodLastName;
        this.prepodPass = prepodPass;
    }

    public Integer getPrepodId() {
        return prepodId;
    }

    public void setPrepodId(Integer prepodId) {
        this.prepodId = prepodId;
    }

    public String getPrepodLogin() {
        return prepodLogin;
    }

    public void setPrepodLogin(String prepodLogin) {
        this.prepodLogin = prepodLogin;
    }

    public String getPrepodFirstName() {
        return prepodFirstName;
    }

    public void setPrepodFirstName(String prepodFirstName) {
        this.prepodFirstName = prepodFirstName;
    }

    public String getPrepodLastName() {
        return prepodLastName;
    }

    public void setPrepodLastName(String prepodLastName) {
        this.prepodLastName = prepodLastName;
    }

    public String getPrepodPass() {
        return prepodPass;
    }

    public void setPrepodPass(String prepodPass) {
        this.prepodPass = prepodPass;
    }

    public static Finder<String, Prepod> find = new Finder<String, Prepod>(String.class, Prepod.class);
}
