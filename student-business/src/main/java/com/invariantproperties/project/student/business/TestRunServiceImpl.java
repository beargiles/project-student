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
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_DELETE;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_ID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_UUID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_LIST;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.TestRunRepository;

/**
 * Implementation of TestRunService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class TestRunServiceImpl implements TestRunService {
    private static final Logger LOG = LoggerFactory.getLogger(TestRunServiceImpl.class);
    private static final String TEST_RUN = "testRun";
    private static final String TEST_RUNS = "testRuns";
    private static final String COULD_NOT_FIND_MESSAGE = "could not find testRun: ";

    @Resource
    private TestRunRepository testRunRepository;

    /**
     * Default constructor
     */
    public TestRunServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    TestRunServiceImpl(TestRunRepository testRunRepository) {
        this.testRunRepository = testRunRepository;
    }

    /**
     * @see com.invariantproperties.project.student.business.TestRunService#
     *      findAllTestRuns()
     */
    @Transactional(readOnly = true)
    @Override
    public List<TestRun> findAllTestRuns() {
        List<TestRun> testRuns = Collections.emptyList();
        try {
            testRuns = testRunRepository.findAll();
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_LIST.format(TEST_RUNS);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_LIST.format(TEST_RUNS);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        }

        return testRuns;
    }

    /**
     * @see com.invariantproperties.project.student.business.TestRunService#
     *      findTestRunById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public TestRun findTestRunById(Integer id) {
        TestRun testRun = null;
        try {
            testRun = testRunRepository.findOne(id);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(TEST_RUN);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(TEST_RUN);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        }

        if (testRun == null) {
            throw new ObjectNotFoundException(id);
        }

        return testRun;
    }

    /**
     * @see com.invariantproperties.project.student.business.TestRunService#
     *      findTestRunByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public TestRun findTestRunByUuid(String uuid) {
        TestRun testRun = null;
        try {
            testRun = testRunRepository.findTestRunByUuid(uuid);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(TEST_RUN);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(TEST_RUN);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        }

        if (testRun == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return testRun;
    }

    /**
     * @see com.invariantproperties.project.student.business.TestRunService#
     *      createTestRun()
     */
    @Transactional
    @Override
    public TestRun createTestRun() {
        return createTestRun(generateName());
    }

    /**
     * @see com.invariantproperties.project.student.business.TestRunService#
     *      createTestRun(java.lang.String)
     */
    @Transactional
    @Override
    public TestRun createTestRun(String name) {
        final TestRun testRun = new TestRun();
        testRun.setName(name);

        TestRun actual = null;
        try {
            actual = testRunRepository.saveAndFlush(testRun);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE.format(TEST_RUN);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE.format(TEST_RUN);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.student.business.TestRunService#
     *      deleteTestRun(java.lang.String)
     */
    @Transactional
    @Override
    public void deleteTestRun(String uuid) {
        TestRun testRun = null;
        try {
            testRun = testRunRepository.findTestRunByUuid(uuid);

            if (testRun == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            testRunRepository.delete(testRun);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_DELETE.format(TEST_RUN);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_DELETE.format(TEST_RUN);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e, uuid);
        }
    }

    /**
     * Generate name for test run. This will usually be the test class and
     * method.
     * 
     * @return
     */
    private String generateName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String name = "(test run)";
        if (stack.length > 2) {
            String classname = stack[2].getClassName();
            int idx = classname.lastIndexOf('.');
            if (idx > 0) {
                classname = classname.substring(idx + 1);
            }
            name = String.format("%s#%s()", classname, stack[2].getMethodName());
        }
        return name;
    }
}
