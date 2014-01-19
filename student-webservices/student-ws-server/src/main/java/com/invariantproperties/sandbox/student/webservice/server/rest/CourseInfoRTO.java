/*
 * This code was written by Bear Giles <bgiles@coyotesong.com> and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.webservice.server.rest;

import javax.xml.bind.annotation.XmlRootElement;

import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.util.StudentUtil;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@XmlRootElement
public class CourseInfoRTO implements Validatable {
    private String code;
    private String name;
    private String summary;
    private String description;
    private Integer creditHours;
    private String testUuid;

    public CourseInfoRTO() {

    }

    public CourseInfoRTO(Course course) {
        code = course.getCode();
        name = course.getName();
        summary = course.getSummary();
        description = course.getDescription();
        creditHours = course.getCreditHours();
        testUuid = (course.getTestRun() == null) ? null : course.getTestRun().getUuid();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }

    public String getTestUuid() {
        return testUuid;
    }

    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }

    /**
     * Validate values. No constraints on name but the test-uuid, if present,
     * must be valid.
     */
    @Override
    public boolean validate() {
        // should be non-null on creation, null on updates
        // TODO: check pattern, e.g., \\p{Alpha}{4}-\\p{Digit}{4}
        // if ((code == null) || code.isEmpty()) {
        // return false;
        // }

        if ((name == null) || name.isEmpty()) {
            return false;
        }

        if ((testUuid != null) && !StudentUtil.isPossibleUuid(testUuid)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        // FIXME: this is unsafe!
        return String.format("Course('%s', '%s', %s)", code, name, testUuid);
    }
}
