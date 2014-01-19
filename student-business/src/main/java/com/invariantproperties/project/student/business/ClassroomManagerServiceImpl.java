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
package com.invariantproperties.project.student.business;

import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_CREATE;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_CREATE_FOR_TESTING;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_DELETE;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_UPDATE;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.ClassroomRepository;

/**
 * Implementation of ClassroomService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class ClassroomManagerServiceImpl implements ClassroomManagerService {
    private static final Logger LOG = LoggerFactory
            .getLogger(ClassroomManagerServiceImpl.class);
    private static final String CLASSROOM = "classroom";
    private static final String COULD_NOT_FIND_MESSAGE = "could not find classroom: ";

    @Resource
    private ClassroomRepository classroomRepository;

    /**
     * Default constructor
     */
    public ClassroomManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    ClassroomManagerServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      createClassroom(java.lang.String)
     */
    @Transactional
    @Override
    public Classroom createClassroom(String name) {
        final Classroom classroom = new Classroom();
        classroom.setName(name);

        Classroom actual = null;
        try {
            actual = classroomRepository.saveAndFlush(classroom);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE.format(CLASSROOM);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE.format(CLASSROOM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      createClassroomForTesting(java.lang.String,
     *      com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional
    @Override
    public Classroom createClassroomForTesting(String name, TestRun testRun) {
        final Classroom classroom = new Classroom();
        classroom.setName(name);
        classroom.setTestRun(testRun);

        Classroom actual = null;
        try {
            actual = classroomRepository.saveAndFlush(classroom);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(CLASSROOM);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(CLASSROOM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.ClassroomFinderService.persistence.ClassroomService#
     *      updateClassroom(com.invariantproperties.project.classroom.domain.Classroom,
     *      java.lang.String)
     */
    @Transactional
    @Override
    public Classroom updateClassroom(Classroom classroom, String name) {
        Classroom updated = null;
        try {
            final Classroom actual = classroomRepository
                    .findClassroomByUuid(classroom.getUuid());

            if (actual == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + classroom.getUuid());
                throw new ObjectNotFoundException(classroom.getUuid());
            }

            actual.setName(name);
            updated = classroomRepository.saveAndFlush(actual);
            classroom.setName(name);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_UPDATE.format(CLASSROOM);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e,
                    classroom.getUuid());
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_UPDATE.format(CLASSROOM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e,
                    classroom.getUuid());
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      deleteClassroom(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteClassroom(String uuid, Integer version) {
        Classroom classroom = null;
        try {
            classroom = classroomRepository.findClassroomByUuid(uuid);

            if (classroom == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            classroomRepository.delete(classroom);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_DELETE.format(CLASSROOM);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_DELETE.format(CLASSROOM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e, uuid);
        }
    }
}
