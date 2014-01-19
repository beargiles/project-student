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

import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.InstructorRepository;

/**
 * Implementation of InstructorService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class InstructorManagerServiceImpl implements InstructorManagerService {
    private static final Logger LOG = LoggerFactory
            .getLogger(InstructorManagerServiceImpl.class);
    private static final String INSTRUCTOR = "instructor";
    private static final String COULD_NOT_FIND_MESSAGE = "could not find instructor: ";

    @Resource
    private InstructorRepository instructorRepository;

    /**
     * Default constructor
     */
    public InstructorManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    InstructorManagerServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      createInstructor(java.lang.String, java.lang.String)
     */
    @Transactional
    @Override
    public Instructor createInstructor(String name, String emailAddress) {
        final Instructor instructor = new Instructor();
        instructor.setName(name);
        instructor.setEmailAddress(emailAddress);

        Instructor actual = null;
        try {
            actual = instructorRepository.saveAndFlush(instructor);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      createInstructorForTesting(java.lang.String, java.lang.String,
     *      com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional
    @Override
    public Instructor createInstructorForTesting(String name,
            String emailAddress, TestRun testRun) {
        final Instructor instructor = new Instructor();
        instructor.setName(name);
        instructor.setEmailAddress(emailAddress);
        instructor.setTestRun(testRun);

        Instructor actual = null;
        try {
            actual = instructorRepository.saveAndFlush(instructor);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.InstructorManagerService.persistence.InstructorService#
     *      updateInstructor(com.invariantproperties.project.instructor.domain.Instructor,
     *      java.lang.String, java.lang.String)
     */
    @Transactional
    @Override
    public Instructor updateInstructor(Instructor instructor, String name,
            String emailAddress) {
        Instructor updated = null;
        try {
            final Instructor actual = instructorRepository
                    .findInstructorByUuid(instructor.getUuid());

            if (actual == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + instructor.getUuid());
                throw new ObjectNotFoundException(instructor.getUuid());
            }

            actual.setName(name);
            actual.setEmailAddress(emailAddress);
            updated = instructorRepository.saveAndFlush(actual);
            instructor.setName(name);
            instructor.setEmailAddress(emailAddress);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_UPDATE.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_UPDATE.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e);
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      deleteInstructor(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteInstructor(String uuid, Integer version) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findInstructorByUuid(uuid);

            if (instructor == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            instructorRepository.delete(instructor);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_DELETE.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_DELETE.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e);
        }
    }
}
