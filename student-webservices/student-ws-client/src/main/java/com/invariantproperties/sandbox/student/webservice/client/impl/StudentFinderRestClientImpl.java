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

import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.project.student.webservice.client.StudentFinderRestClient;

/**
 * Implementation of StudentFinderRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentFinderRestClientImpl extends AbstractFinderRestClientImpl<Student> implements
        StudentFinderRestClient {
    private static final Student[] EMPTY_STUDENT_ARRAY = new Student[0];

    /**
     * Constructor.
     * 
     * @param studentResource
     */
    public StudentFinderRestClientImpl(final String resource) {
        super(resource, Student.class, Student[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Student> findAllStudents() {
        Student[] students = super.getAllObjects(EMPTY_STUDENT_ARRAY);
        return Arrays.asList(students);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Student findStudentById(final Integer id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Student findStudentByUuid(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * {@inheritDoc} FIXME: implement!
     */
    @Override
    public Student findStudentByEmailAddress(final String emailAddress) {
        throw new ObjectNotFoundException(null, Student.class, emailAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Student> findStudentsByTestRun(final TestRun testRun) {
        throw new UnsupportedOperationException();
    }
}
