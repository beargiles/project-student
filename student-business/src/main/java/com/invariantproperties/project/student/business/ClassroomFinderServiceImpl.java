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
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_ID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_UUID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_LIST;
import static com.invariantproperties.project.student.specification.ClassroomSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.ClassroomFinderService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.ClassroomRepository;

/**
 * Implementation of ClassroomService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class ClassroomFinderServiceImpl implements ClassroomFinderService {
    private static final Logger LOG = LoggerFactory.getLogger(ClassroomFinderServiceImpl.class);
    private static final String CLASSROOM = "classroom";
    private static final String CLASSROOMS = "classrooms";

    @Resource
    private ClassroomRepository classroomRepository;

    /**
     * Default constructor
     */
    public ClassroomFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    ClassroomFinderServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
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
            count = classroomRepository.count(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_COUNT.format(CLASSROOMS) + testRun;
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_COUNT.format(CLASSROOMS) + testRun;
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      findAllClassrooms()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Classroom> findAllClassrooms() {
        return findClassroomsByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      findClassroomById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Classroom findClassroomById(Integer id) {
        Classroom classroom = null;
        try {
            classroom = classroomRepository.findOne(id);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(CLASSROOM);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(CLASSROOM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        }

        if (classroom == null) {
            throw new ObjectNotFoundException(id);
        }

        return classroom;
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      findClassroomByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Classroom findClassroomByUuid(String uuid) {
        Classroom classroom = null;
        try {
            classroom = classroomRepository.findClassroomByUuid(uuid);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(CLASSROOM);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(CLASSROOM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        }

        if (classroom == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return classroom;
    }

    /**
     * @see com.invariantproperties.project.student.business.ClassroomFinderService#
     *      findClassroomsByTestRun(com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Classroom> findClassroomsByTestRun(TestRun testRun) {
        List<Classroom> classrooms = null;

        try {
            classrooms = classroomRepository.findAll(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_LIST.format(CLASSROOMS);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_LIST.format(CLASSROOMS);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        }

        return classrooms;
    }
}
