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

import java.util.Arrays;
import java.util.List;

import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.CourseFinderRestClient;
import com.invariantproperties.project.student.webservice.client.ObjectNotFoundException;

/**
 * Implementation of CourseRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseFinderRestClientImpl extends AbstractFinderRestClientImpl<Course> implements CourseFinderRestClient {
    private static final Course[] EMPTY_COURSE_ARRAY = new Course[0];

    /**
     * Constructor.
     * 
     * @param courseResource
     */
    public CourseFinderRestClientImpl(final String resource) {
        super(resource, Course.class, Course[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Course> findAllCourses() {
        final Course[] courses = super.getAllObjects(EMPTY_COURSE_ARRAY);
        return Arrays.asList(courses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course findCourseById(final Integer id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course findCourseByUuid(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * @{inheritDoc FIXME: implement!
     */
    @Override
    public Course findCourseByCode(final String code) {
        throw new ObjectNotFoundException(null, Course.class, code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Course> findCoursesByTestRun(final TestRun testRun) {
        throw new UnsupportedOperationException();
    }
}
