package com.invariantproperties.project.student.domain;

import java.security.CodeSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stack trace element augmented with code source. Used to record exceptions.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
// @XmlRootElement
// @Entity
// @Table(name = "stack_trace_element")
// @AttributeOverride(name = "id", column = @Column(name =
// "stack_trace_element_pkey"))
public class PersistentStackTraceElement extends PersistentObject {
    private static final long serialVersionUID = 1;
    private PersistentStackTrace stackTrace;
    private String className;
    private String fileName;
    private Integer lineNumber;
    private String methodName;
    private Boolean isNativeMethod;
    private String location;
    private String hash;
    private Integer seqno;
    private final Map<String, CodeSource> sources = new ConcurrentHashMap<>();

    public PersistentStackTraceElement() {

    }

    public PersistentStackTraceElement(java.lang.StackTraceElement element,
            int seqno) {
        this.className = element.getClassName();
        this.fileName = element.getFileName();
        this.lineNumber = element.getLineNumber();
        this.methodName = element.getMethodName();
        this.isNativeMethod = element.isNativeMethod();
        this.seqno = seqno;

        try {
            this.location = find(className).getLocation().getFile();
        } catch (ClassNotFoundException e) {
            // TODO log this
        }
    }

    public PersistentStackTrace getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(PersistentStackTrace stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Boolean getIsNativeMethod() {
        return isNativeMethod;
    }

    public void setIsNativeMethod(Boolean isNativeMethod) {
        this.isNativeMethod = isNativeMethod;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getSeqno() {
        return seqno;
    }

    public void setSeqno(Integer seqno) {
        this.seqno = seqno;
    }

    public Map<String, CodeSource> getSources() {
        return sources;
    }

    /**
     * Find CodeSource for specified class name.
     * 
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    CodeSource find(String className) throws ClassNotFoundException {
        if (!sources.containsKey(className)) {
            sources.put(className, Thread.currentThread()
                    .getContextClassLoader().loadClass(className)
                    .getProtectionDomain().getCodeSource());
        }
        return sources.get(className);
    }
}
