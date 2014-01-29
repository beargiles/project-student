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
package com.invariantproperties.project.student.webservice.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.project.student.webservice.client.RestClientException;
import com.invariantproperties.project.student.webservice.client.StudentFinderRestClient;
import com.invariantproperties.project.student.webservice.client.StudentManagerRestClient;
import com.invariantproperties.project.student.webservice.client.TestRunManagerRestClient;
import com.invariantproperties.project.student.webservice.client.impl.StudentFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.impl.StudentManagerRestClientImpl;
import com.invariantproperties.project.student.webservice.client.impl.TestRunManagerRestClientImpl;
import com.invariantproperties.project.student.webservice.config.TestRestApplicationContext;

/**
 * Integration tests for StudentResource
 * 
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext.class })
public class StudentRestServerIntegrationTest {
    @Resource
    private String resourceBase;
    private StudentFinderRestClient finderClient;
    private StudentManagerRestClient managerClient;
    private TestRunManagerRestClient testClient;

    @Before
    public void init() {
        this.finderClient = new StudentFinderRestClientImpl(resourceBase + "student/");
        this.managerClient = new StudentManagerRestClientImpl(resourceBase + "student/");
        this.testClient = new TestRunManagerRestClientImpl(resourceBase + "testRun/");
    }

    @Test
    public void testGetAll() throws IOException {
        final List<Student> students = finderClient.findAllStudents();
        assertNotNull(students);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownStudent() throws IOException {
        finderClient.findStudentByUuid("11111111-1111-1111-1111-111111111111");
    }

    @Test(expected = RestClientException.class)
    public void testBadStudentUuid() throws IOException {
        finderClient.findStudentByUuid("bad-uuid");
    }

    @Test
    public void testLifecycle() throws IOException {
        final TestRun testRun = testClient.createTestRun();

        final String davidName = "David : " + testRun.getUuid();
        final String davidEmail = "david-" + testRun.getUuid() + "@example.com";
        final Student expected = managerClient.createStudent(davidName, davidEmail);
        assertEquals(davidName, expected.getName());
        assertEquals(davidEmail, expected.getEmailAddress());

        final Student actual1 = finderClient.findStudentByUuid(expected.getUuid());
        assertEquals(davidName, actual1.getName());
        assertEquals(davidEmail, actual1.getEmailAddress());

        final List<Student> students = finderClient.findAllStudents();
        assertTrue(students.size() > 0);

        final String edithName = "Edith : " + testRun.getUuid();
        final String edithEmail = "edith-" + testRun.getUuid() + "@example.com";
        final Student actual2 = managerClient.updateStudent(actual1.getUuid(), edithName, edithEmail);
        assertEquals(edithName, actual2.getName());
        assertEquals(edithEmail, actual2.getEmailAddress());

        managerClient.deleteStudent(actual1.getUuid(), 1);
        try {
            finderClient.findStudentByUuid(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }

        testClient.deleteTestRun(testRun.getUuid(), 1);
    }
}
