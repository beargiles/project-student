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
 * specific language governing pestRunissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.persistence.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.config.BusinessApplicationContext;
import com.invariantproperties.project.student.config.TestBusinessApplicationContext;
import com.invariantproperties.project.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.project.student.domain.TestRun;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class TestRunServiceIntegrationTest {

    @Resource
    private TestRunService dao;

    @Resource
    TestRunService testService;

    @Test
    public void testTestRunLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final TestRun expected = new TestRun();

        assertNull(expected.getId());

        // create testRun
        TestRun actual = dao.createTestRun();
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        // assertThat(expected, equalTo(actual));
        assertNotNull(actual.getName());
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get testRun by id
        actual = dao.findTestRunById(expected.getId());
        // assertThat(expected, equalTo(actual));
        assertEquals(expected.getId(), actual.getId());

        // get testRun by uuid
        actual = dao.findTestRunByUuid(expected.getUuid());
        // assertThat(expected, equalTo(actual));
        assertEquals(expected.getId(), actual.getId());

        // delete TestRun
        dao.deleteTestRun(expected.getUuid());
        try {
            dao.findTestRunByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findTestRunById() with unknown testRun.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindTestRunByIdWhenTestRunIsNotKnown() {
        final Integer id = 1;
        dao.findTestRunById(id);
    }

    /**
     * @test findTestRunByUuid() with unknown TestRun.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindTestRunByUuidWhenTestRunIsNotKnown() {
        final String uuid = "missing";
        dao.findTestRunByUuid(uuid);
    }

    /**
     * Test deleteTestRun() with unknown testRun.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTestRunWhenTestRunIsNotFound() {
        dao.deleteTestRun("missing");
    }
}