package es.test.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import es.test.daos.IdentifiableEntity;

@Entity
@Table(name = "TASKS")
public class Task implements Serializable, IdentifiableEntity<Integer> {

    private static final long serialVersionUID = -5702198920444295842L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATE")
    private Date date = new Date(Calendar.getInstance().getTime().getTime());

    @Column(name = "STATUS")
    private TaskStatus status = TaskStatus.waiting;

    @Column(name = "COMPLETION_FACTOR")
    private double completionFactor;

    public Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer value) {
        assert value != null;
        id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date value) {
        date = value;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus value) {
        status = value;
    }

    public double getCompletionFactor() {
        return completionFactor;
    }

    public void setCompletionFactor(double value) {
        completionFactor = value;
    }
}
