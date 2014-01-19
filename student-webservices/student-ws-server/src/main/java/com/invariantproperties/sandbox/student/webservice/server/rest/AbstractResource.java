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

import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public abstract class AbstractResource {

    public String scrubCount(final long count) {
        return String.format("{ \"count\": %d }", count);
    }

    public Classroom scrubClassroom(final Classroom dirty) {
        final Classroom clean = new Classroom();
        clean.setUuid(dirty.getUuid());
        clean.setName(dirty.getName());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }

    public Course scrubCourse(final Course dirty) {
        final Course clean = new Course();
        clean.setUuid(dirty.getUuid());
        clean.setCode(dirty.getCode());
        clean.setName(dirty.getName());
        clean.setSummary(dirty.getSummary());
        clean.setDescription(dirty.getDescription());
        clean.setCreditHours(dirty.getCreditHours());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }

    public Instructor scrubInstructor(final Instructor dirty) {
        final Instructor clean = new Instructor();
        clean.setUuid(dirty.getUuid());
        clean.setName(dirty.getName());
        clean.setEmailAddress(dirty.getEmailAddress());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }

    public Section scrubSection(final Section dirty) {
        final Section clean = new Section();
        clean.setUuid(dirty.getUuid());
        clean.setName(dirty.getName());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }

    public Student scrubStudent(final Student dirty) {
        final Student clean = new Student();
        clean.setUuid(dirty.getUuid());
        clean.setName(dirty.getName());
        clean.setEmailAddress(dirty.getEmailAddress());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }

    public Term scrubTerm(final Term dirty) {
        final Term clean = new Term();
        clean.setUuid(dirty.getUuid());
        clean.setName(dirty.getName());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }

    public TestRun scrubTestRun(final TestRun dirty) {
        final TestRun clean = new TestRun();
        clean.setUuid(dirty.getUuid());
        clean.setName(dirty.getName());
        clean.setVersion(dirty.getVersion());
        clean.setCreationDate(dirty.getCreationDate());
        // clean.setSelf("resource/" + dirty.getUuid());
        return clean;
    }
}
