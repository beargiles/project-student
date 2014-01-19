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
import static com.invariantproperties.project.student.specification.StudentSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.StudentFinderService;
import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.StudentRepository;

/**
 * Implementation of StudentService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class StudentFinderServiceImpl implements StudentFinderService {
    private static final Logger LOG = LoggerFactory.getLogger(StudentFinderServiceImpl.class);
    private static final String STUDENT = "student";
    private static final String STUDENTS = "students";

    @Resource
    private StudentRepository studentRepository;

    /**
     * Default constructor
     */
    public StudentFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    StudentFinderServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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
            count = studentRepository.count(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_COUNT.format(STUDENTS) + testRun;
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_COUNT.format(STUDENTS) + testRun;
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.project.student.business.StudentFinderService#
     *      findAllStudents()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Student> findAllStudents() {
        return findStudentsByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.StudentFinderService#
     *      findStudentById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Student findStudentById(Integer id) {
        Student student = null;
        try {
            student = studentRepository.findOne(id);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(STUDENT);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(STUDENT);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        }

        if (student == null) {
            throw new ObjectNotFoundException(id);
        }

        return student;
    }

    /**
     * @see com.invariantproperties.project.student.business.StudentFinderService#
     *      findStudentByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Student findStudentByUuid(String uuid) {
        Student student = null;
        try {
            student = studentRepository.findStudentByUuid(uuid);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(STUDENT);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(STUDENT);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        }

        if (student == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return student;
    }

    /**
     * @see com.invariantproperties.project.student.business.StudentFinderService#
     *      findStudentsByTestRun(com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Student> findStudentsByTestRun(TestRun testRun) {
        List<Student> students = null;

        try {
            students = studentRepository.findAll(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_LIST.format(STUDENTS);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_LIST.format(STUDENTS);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        }

        return students;
    }

    /**
     * @see com.invariantproperties.project.student.business.StudentFinderService#
     *      findStudentByEmailAddress(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Student findStudentByEmailAddress(String emailAddress) {
        Student student = null;
        try {
            student = studentRepository.findStudentByEmailAddress(emailAddress);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_EMAIL_ADDRESS.format(STUDENT);
            throw new PersistenceException(UNABLE_TO_FIND_BY_EMAIL_ADDRESS, msg, e, emailAddress);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_EMAIL_ADDRESS.format(STUDENT);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_EMAIL_ADDRESS, msg, e, emailAddress);
        }

        if (student == null) {
            throw new ObjectNotFoundException("(" + emailAddress + ")");
        }

        return student;
    }
}
