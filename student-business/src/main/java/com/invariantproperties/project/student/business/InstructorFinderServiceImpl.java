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

import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_COUNT;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_EMAIL_ADDRESS;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_ID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_UUID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_LIST;
import static com.invariantproperties.project.student.specification.InstructorSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.InstructorFinderService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.InstructorRepository;

/**
 * Implementation of InstructorService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class InstructorFinderServiceImpl implements InstructorFinderService {
    private static final Logger LOG = LoggerFactory.getLogger(InstructorFinderServiceImpl.class);
    private static final String INSTRUCTOR = "instructor";
    private static final String INSTRUCTORS = "instructors";

    @Resource
    private InstructorRepository instructorRepository;

    /**
     * Default constructor
     */
    public InstructorFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    InstructorFinderServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    /**
     * @see com.invariantproperties.project.student.business.FinderService#
     *      count()
     */
    @Transactional(readOnly = true)
    @Override
    public long count() {
        return countByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.FinderService#
     *      countByTestRun(com.invariantproperties.project.student.domain.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public long countByTestRun(TestRun testRun) {
        long count = 0;
        try {
            count = instructorRepository.count(testRunIs(testRun));
        } catch (UnitTestException e) {
            String msg = UNABLE_TO_COUNT.format(INSTRUCTORS) + testRun;
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        } catch (DataAccessException e) {
            String msg = UNABLE_TO_COUNT.format(INSTRUCTORS) + testRun;
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      findAllInstructors()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Instructor> findAllInstructors() {
        return findInstructorsByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      findInstructorById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Instructor findInstructorById(Integer id) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findOne(id);
        } catch (UnitTestException e) {
            String msg = UNABLE_TO_FIND_BY_ID.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        } catch (DataAccessException e) {
            String msg = UNABLE_TO_FIND_BY_ID.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        }

        if (instructor == null) {
            throw new ObjectNotFoundException(id);
        }

        return instructor;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      findInstructorByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Instructor findInstructorByUuid(String uuid) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findInstructorByUuid(uuid);
        } catch (UnitTestException e) {
            String msg = UNABLE_TO_FIND_BY_UUID.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        } catch (DataAccessException e) {
            String msg = UNABLE_TO_FIND_BY_UUID.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        }

        if (instructor == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return instructor;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      findInstructorsByTestRun(com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Instructor> findInstructorsByTestRun(TestRun testRun) {
        List<Instructor> instructors = null;

        try {
            instructors = instructorRepository.findAll(testRunIs(testRun));
        } catch (UnitTestException e) {
            String msg = UNABLE_TO_LIST.format(INSTRUCTORS);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        } catch (DataAccessException e) {
            String msg = UNABLE_TO_LIST.format(INSTRUCTORS);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        }

        return instructors;
    }

    /**
     * @see com.invariantproperties.project.student.business.InstructorManagerService#
     *      findInstructorByEmailAddress(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Instructor findInstructorByEmailAddress(String emailAddress) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findInstructorByEmailAddress(emailAddress);
        } catch (UnitTestException e) {
            String msg = UNABLE_TO_FIND_BY_UUID.format(INSTRUCTOR);
            throw new PersistenceException(UNABLE_TO_FIND_BY_EMAIL_ADDRESS, msg, e, emailAddress);
        } catch (DataAccessException e) {
            String msg = UNABLE_TO_FIND_BY_UUID.format(INSTRUCTOR);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_EMAIL_ADDRESS, msg, e, emailAddress);
        }

        if (instructor == null) {
            throw new ObjectNotFoundException("(" + emailAddress + ")");
        }

        return instructor;
    }
}
