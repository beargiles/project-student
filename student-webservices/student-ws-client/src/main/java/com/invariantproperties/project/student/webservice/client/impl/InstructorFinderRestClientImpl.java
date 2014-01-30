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

import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.InstructorFinderRestClient;
import com.invariantproperties.project.student.webservice.client.ObjectNotFoundException;

/**
 * Implementation of InstructoFinderRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class InstructorFinderRestClientImpl extends AbstractFinderRestClientImpl<Instructor> implements
        InstructorFinderRestClient {
    private static final Instructor[] EMPTY_INSTRUCTOR_ARRAY = new Instructor[0];

    /**
     * Constructor.
     * 
     * @param instructorResource
     */
    public InstructorFinderRestClientImpl(final String resource) {
        super(resource, Instructor.class, Instructor[].class);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public List<Instructor> findAllInstructors() {
        final Instructor[] instructors = super.getAllObjects(EMPTY_INSTRUCTOR_ARRAY);
        return Arrays.asList(instructors);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Instructor findInstructorById(final Integer id) {
        throw new UnsupportedOperationException();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Instructor findInstructorByUuid(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * @{inheritDoc FIXME: implement!
     */
    @Override
    public Instructor findInstructorByEmailAddress(final String code) {
        throw new ObjectNotFoundException(null, Instructor.class, code);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public List<Instructor> findInstructorsByTestRun(final TestRun testRun) {
        throw new UnsupportedOperationException();
    }
}
