package com.invariantproperties.project.student.domain;


/**
 * Exception. N.B., some exceptions have a 'next exception' in addition to the
 * 'cause', e.g., SQLException. This class does not model this and that might
 * cause loss of information.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
// @XmlRootElement
// @Entity
// @Table(name = "throwable")
// @AttributeOverride(name = "id", column = @Column(name = "throwable_pkey"))
public class PersistentThrowable extends PersistentObject {
    private static final long serialVersionUID = 1;
    private String message;
    private PersistentThrowable cause;
    private PersistentStackTrace stackTrace;
    private String jiraIssue;
    private String status;
}
