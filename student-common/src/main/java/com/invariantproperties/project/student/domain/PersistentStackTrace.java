package com.invariantproperties.project.student.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Stack trace element augmented with code source. Used to record exceptions.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
// @XmlRootElement
// @Entity
// @Table(name = "stack_trace")
// @AttributeOverride(name = "id", column = @Column(name = "stack_trace_pkey"))
public class PersistentStackTrace extends PersistentObject {
    private static final long serialVersionUID = 1;
    private List<PersistentThrowable> throwables = new ArrayList<>();
    private List<PersistentStackTraceElement> elements = new ArrayList<>();

    public List<PersistentThrowable> getThrowables() {
        return throwables;
    }

    public void setThrowables(List<PersistentThrowable> throwables) {
        this.throwables = throwables;
    }

    public List<PersistentStackTraceElement> getElements() {
        return elements;
    }

    public void setElements(List<PersistentStackTraceElement> elements) {
        this.elements = elements;
    }
}
