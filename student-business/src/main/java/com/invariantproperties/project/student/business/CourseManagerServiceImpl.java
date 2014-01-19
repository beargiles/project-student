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

import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.CourseRepository;

/**
 * Implementation of CourseService.
 * 
 * FIXME - add validation that credit hours >= 0
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class CourseManagerServiceImpl implements CourseManagerService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseManagerServiceImpl.class);
    private static final String COURSE = "course";
    private static final String COULD_NOT_FIND_MESSAGE = "could not find course: ";

    @Resource
    private CourseRepository courseRepository;

    /**
     * Default constructor
     */
    public CourseManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    CourseManagerServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * @see 
     *      com.invariantproperties.project.student.business.CourseFinderService#
     *      createCourse(...)
     */
    @Transactional
    @Override
    public Course createCourse(String code, String name, String summary, String description, Integer creditHours) {
        final Course course = new Course();
        course.setCode(code);
        course.setName(name);
        course.setSummary(summary);
        course.setDescription(description);
        course.setCreditHours(creditHours);

        Course actual = null;
        try {
            actual = courseRepository.saveAndFlush(course);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE.format(COURSE);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        }

        return actual;
    }

    /**
     * @see 
     *      com.invariantproperties.project.student.business.CourseFinderService#
     *      createCourseForTesting(...)
     */
    @Transactional
    @Override
    public Course createCourseForTesting(String code, String name, String summary, String description,
            Integer creditHours, TestRun testRun) {
        final Course course = new Course();
        course.setCode(code);
        course.setName(name);
        course.setSummary(summary);
        course.setDescription(description);
        course.setCreditHours(creditHours);
        course.setTestRun(testRun);

        Course actual = null;
        try {
            actual = courseRepository.saveAndFlush(course);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(COURSE);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.CourseFinderService.persistence.
     *      CourseService# updateCourse(...)
     */
    @Transactional
    @Override
    public Course updateCourse(Course course, String name, String summary, String description, Integer creditHours) {
        Course updated = null;
        try {
            final Course actual = courseRepository.findCourseByUuid(course.getUuid());

            if (actual == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + course.getUuid());
                throw new ObjectNotFoundException(course.getUuid());
            }

            actual.setName(name);
            actual.setSummary(summary);
            actual.setDescription(description);
            actual.setCreditHours(creditHours);
            updated = courseRepository.saveAndFlush(actual);
            course.setName(name);
            course.setSummary(summary);
            course.setDescription(description);
            course.setCreditHours(creditHours);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_UPDATE.format(COURSE);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e, course.getUuid());
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_UPDATE.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e, course.getUuid());
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.project.student.business.CourseFinderService#
     *      deleteCourse(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteCourse(String uuid, Integer version) {
        Course course = null;
        try {
            course = courseRepository.findCourseByUuid(uuid);

            if (course == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            courseRepository.delete(course);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_DELETE.format(COURSE);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_DELETE.format(COURSE);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e, uuid);
        }
    }
}
