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
package com.invariantproperties.project.student.webservice.client.impl;

import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractManagerRestClientImpl;
import com.invariantproperties.project.student.webservice.client.CourseManagerRestClient;

/**
 * Implementation of CourseManagerRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseManagerRestClientImpl extends AbstractManagerRestClientImpl<Course> implements
        CourseManagerRestClient {

    /**
     * Constructor.
     * 
     * @param courseResource
     */
    public CourseManagerRestClientImpl(final String resource) {
        super(resource, Course.class);
    }

    /**
     * Create JSON string.
     * 
     * @param code
     * @param name
     * @param summary
     * @param description
     * @param creditHours
     * @param testRun
     * @return
     */
    String createJson(final String code, final String name, final String summary, final String description,
            final Integer creditHours, final TestRun testRun) {
        StringBuilder json = new StringBuilder("{ ");
        boolean first = true;
        if (code != null) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(String.format("\"code\": \"%s\"", code));
        }

        if (name != null) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(String.format("\"name\": \"%s\"", name));
        }

        if (summary != null) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(String.format("\"summary\": \"%s\"", summary));
        }

        if (description != null) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(String.format("\"description\": \"%s\"", description));
        }

        if (creditHours != null) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(String.format("\"creditHours\": \"%s\"", creditHours));
        }

        if (testRun != null && testRun.getUuid() != null) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(String.format("\"testUuid\": \"%s\"", testRun.getUuid()));
        }

        json.append(" }");

        return json.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course createCourse(final String code, final String name, final String summary, final String description,
            final Integer creditHours) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("'code' is required");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return createObject(createJson(code, name, summary, description, creditHours, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course createCourseForTesting(final String code, final String name, final String summary,
            final String description, final Integer creditHours, final TestRun testRun) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (testRun == null || testRun.getUuid() == null || testRun.getUuid().isEmpty()) {
            throw new IllegalArgumentException("'testRun' is required");
        }

        return createObject(createJson(code, name, summary, description, creditHours, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course updateCourse(final String uuid, final String name, final String summary, final String description,
            final Integer creditHours) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return super.updateObject(createJson(null, name, summary, description, creditHours, null), uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course updateCourse(Course course, String name, String summary, String description, Integer creditHours) {
        return updateCourse(course.getUuid(), name, summary, description, creditHours);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCourse(String uuid, Integer version) {
        super.deleteObject(uuid, version);
    }
}
