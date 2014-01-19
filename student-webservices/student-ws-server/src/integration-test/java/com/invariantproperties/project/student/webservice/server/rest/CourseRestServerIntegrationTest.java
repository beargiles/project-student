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

import static com.invariantproperties.project.student.matcher.CourseEquality.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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

import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.CourseFinderRestClient;
import com.invariantproperties.project.student.webservice.client.CourseManagerRestClient;
import com.invariantproperties.project.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.project.student.webservice.client.RestClientException;
import com.invariantproperties.project.student.webservice.client.TestRunManagerRestClient;
import com.invariantproperties.project.student.webservice.client.impl.CourseFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.impl.CourseManagerRestClientImpl;
import com.invariantproperties.project.student.webservice.client.impl.TestRunManagerRestClientImpl;
import com.invariantproperties.project.student.webservice.config.TestRestApplicationContext;

/**
 * Integration tests for CourseResource
 * 
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext.class })
public class CourseRestServerIntegrationTest {

    @Resource
    private String resourceBase;
    private CourseFinderRestClient finderClient;
    private CourseManagerRestClient managerClient;
    private TestRunManagerRestClient testClient;

    @Before
    public void init() {
        this.finderClient = new CourseFinderRestClientImpl(resourceBase + "course/");
        this.managerClient = new CourseManagerRestClientImpl(resourceBase + "course/");
        this.testClient = new TestRunManagerRestClientImpl(resourceBase + "testRun/");
    }

    @Test
    public void testGetAll() throws IOException {
        final List<Course> courses = finderClient.findAllCourses();
        assertNotNull(courses);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownCourse() throws IOException {
        finderClient.findCourseByUuid("11111111-1111-1111-1111-111111111111");
    }

    @Test(expected = RestClientException.class)
    public void testBadCourseUuid() throws IOException {
        finderClient.findCourseByUuid("bad-uuid");
    }

    @Test
    public void testLifecycle() throws IOException {
        final TestRun testRun = testClient.createTestRun();

        final String code = "PHYS101";
        final String name = "Physics 201 : " + testRun.getUuid();
        final String summary = "summary 1";
        final String description = "description 1";
        final Integer hours = 1;

        final Course expected = new Course();
        expected.setCode(code);
        expected.setName(name);
        expected.setSummary(summary);
        expected.setDescription(description);
        expected.setCreditHours(hours);

        final Course actual = managerClient.createCourse(code, name, summary, description, hours);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());
        assertThat(expected, equalTo(actual));

        final Course actual1 = finderClient.findCourseByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual1));

        final List<Course> courses = finderClient.findAllCourses();
        assertTrue(courses.size() > 0);

        final String name2 = "Newtonian Mechanics 201 : " + testRun.getUuid();
        final String summary2 = "summary 2";
        final String description2 = "description 2";
        final Integer hours2 = 2;
        expected.setName(name2);
        expected.setSummary(summary2);
        expected.setDescription(description2);
        expected.setCreditHours(hours2);

        final Course actual2 = managerClient.updateCourse(actual1.getUuid(), name2, summary2, description2, hours2);
        assertThat(expected, equalTo(actual2));

        managerClient.deleteCourse(actual1.getUuid(), 1);
        try {
            finderClient.findCourseByUuid(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }

        testClient.deleteTestRun(testRun.getUuid(), 1);
    }
}
