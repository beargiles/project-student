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
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_CODE;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_ID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_UUID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_LIST;
import static com.invariantproperties.project.student.specification.CourseSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.CourseRepository;

/**
 * Implementation of CourseService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class CourseFinderServiceImpl implements CourseFinderService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseFinderServiceImpl.class);
    private static final String COURSE = "course";
    private static final String COURSES = "courses";

    @Resource
    private CourseRepository courseRepository;

    /**
     * Default constructor
     */
    public CourseFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    CourseFinderServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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
            count = courseRepository.count(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_COUNT.format(COURSES) + testRun;
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_COUNT.format(COURSES) + testRun;
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.project.student.business.CourseFinderService#
     *      findAllCourses()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllCourses() {
        return findCoursesByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.CourseFinderService#
     *      findCourseById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseById(Integer id) {
        Course course = null;
        try {
            course = courseRepository.findOne(id);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(COURSE);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        }

        if (course == null) {
            throw new ObjectNotFoundException(id);
        }

        return course;
    }

    /**
     * @see com.invariantproperties.project.student.business.CourseFinderService#
     *      findCourseByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseByUuid(String uuid) {
        Course course = null;
        try {
            course = courseRepository.findCourseByUuid(uuid);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(COURSE);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        }

        if (course == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return course;
    }

    /**
     * @see com.invariantproperties.project.student.business.CourseFinderService#
     *      findCourseByCode(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseByCode(String code) {
        Course course = null;
        try {
            course = courseRepository.findCourseByCode(code);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_CODE.format(COURSE);
            throw new PersistenceException(UNABLE_TO_FIND_BY_CODE, msg, e, code);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_CODE.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_CODE, msg, e, code);
        }

        if (course == null) {
            throw new ObjectNotFoundException(code);
        }

        return course;
    }

    /**
     * @see com.invariantproperties.project.student.business.CourseFinderService#
     *      findCoursesByTestRun(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Course> findCoursesByTestRun(TestRun testRun) {
        List<Course> courses = null;

        try {
            courses = courseRepository.findAll(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_LIST.format(COURSES);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_LIST.format(COURSES);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        }

        return courses;
    }
}
