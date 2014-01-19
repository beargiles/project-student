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

import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractManagerRestClientImpl;
import com.invariantproperties.project.student.webservice.client.ClassroomManagerRestClient;

/**
 * Implementation of ClassroomManagerRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ClassroomManagerRestClientImpl extends AbstractManagerRestClientImpl<Classroom> implements
        ClassroomManagerRestClient {

    /**
     * Constructor.
     * 
     * @param classroomResource
     */
    public ClassroomManagerRestClientImpl(final String resource) {
        super(resource, Classroom.class);
    }

    /**
     * Create JSON string.
     * 
     * @param name
     * @return
     */
    String createJson(final String name) {
        return String.format("{ \"name\": \"%s\" }", name);
    }

    /**
     * Create JSON string.
     * 
     * @param name
     * @param testUuid
     * @return
     */
    String createJson(final String name, final TestRun testRun) {
        return String.format("{ \"name\": \"%s\", \"testUuid\": \"%s\" }", name, testRun.getUuid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom createClassroom(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return createObject(createJson(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom createClassroomForTesting(final String name, final TestRun testRun) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (testRun == null || testRun.getUuid() == null || testRun.getUuid().isEmpty()) {
            throw new IllegalArgumentException("'testRun' is required");
        }

        return createObject(createJson(name, testRun));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom updateClassroom(final String uuid, final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return super.updateObject(createJson(name), uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom updateClassroom(Classroom classroom, String name) {
        return updateClassroom(classroom.getUuid(), name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClassroom(String uuid, Integer version) {
        super.deleteObject(uuid, version);
    }
}
