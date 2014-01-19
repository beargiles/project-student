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
package com.invariantproperties.project.student.persistence.config;

import static com.invariantproperties.project.student.matcher.TermEquality.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TermFinderService;
import com.invariantproperties.project.student.business.TermManagerService;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.config.BusinessApplicationContext;
import com.invariantproperties.project.student.config.TestBusinessApplicationContext;
import com.invariantproperties.project.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class TermServiceIntegrationTest {

    @Resource
    private TermFinderService fdao;

    @Resource
    private TermManagerService mdao;

    @Resource
    TestRunService testService;

    @Test
    public void testTermLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final String name = "Fall 2013 : " + testRun.getUuid();

        final Term expected = new Term();
        expected.setName(name);

        assertNull(expected.getId());

        // create term
        Term actual = mdao.createTermForTesting(name, testRun);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get term by id
        actual = fdao.findTermById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get term by uuid
        actual = fdao.findTermByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // get all terms
        final List<Term> terms = fdao.findTermsByTestRun(testRun);
        assertTrue(terms.contains(actual));

        // count terms
        final long count = fdao.countByTestRun(testRun);
        assertTrue(count > 0);

        // update term
        expected.setName("Fall 2014 : " + testRun.getUuid());
        actual = mdao.updateTerm(actual, expected.getName());
        assertThat(expected, equalTo(actual));

        // verify testRun.getObjects
        // final List<TestablePersistentObject> objects = testRun.getObjects();
        // assertTrue(objects.contains(actual));

        // delete Term
        mdao.deleteTerm(expected.getUuid(), 0);
        try {
            fdao.findTermByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findTermById() with unknown term.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindTermByIdWhenTermIsNotKnown() {
        final Integer id = 1;
        fdao.findTermById(id);
    }

    /**
     * @test findTermByUuid() with unknown Term.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindTermByUuidWhenTermIsNotKnown() {
        final String uuid = "missing";
        fdao.findTermByUuid(uuid);
    }

    /**
     * Test updateTerm() with unknown term.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateTermWhenTermIsNotFound() {
        final Term term = new Term();
        term.setUuid("missing");
        mdao.updateTerm(term, "Fall 2014");
    }

    /**
     * Test deleteTerm() with unknown term.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTermWhenTermIsNotFound() {
        mdao.deleteTerm("missing", 0);
    }
}