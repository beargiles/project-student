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

import static com.invariantproperties.project.student.matcher.CourseEquality.equalTo;
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

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.config.BusinessApplicationContext;
import com.invariantproperties.project.student.config.TestBusinessApplicationContext;
import com.invariantproperties.project.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class CourseServiceIntegrationTest {

    @Resource
    private CourseFinderService fdao;

    @Resource
    private CourseManagerService mdao;

    @Resource
    private TestRunService testService;

    @Test
    public void testCourseLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final String code = "MATH101";
        final String name = "Calculus 101 name : " + testRun.getUuid();
        final String summary = "summary 1";
        final String description = "description 1";
        final Integer hours = 1;

        final Course expected = new Course();
        expected.setCode(code);
        expected.setName(name);
        expected.setSummary(summary);
        expected.setDescription(description);
        expected.setCreditHours(hours);

        assertNull(expected.getId());

        // create course
        Course actual = mdao.createCourseForTesting(code, name, summary, description, hours, testRun);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get course by id
        actual = fdao.findCourseById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get course by uuid
        actual = fdao.findCourseByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // get course by code
        actual = fdao.findCourseByCode(expected.getCode());
        assertThat(expected, equalTo(actual));

        // get all courses
        final List<Course> courses = fdao.findCoursesByTestRun(testRun);
        assertTrue(courses.contains(actual));

        // count courses
        final long count = fdao.countByTestRun(testRun);
        assertTrue(count > 0);

        // update course
        expected.setName("Calculus 102 : " + testRun.getUuid());
        expected.setSummary("summary 2");
        expected.setDescription("description 2");
        expected.setCreditHours(2);
        actual = mdao.updateCourse(actual, expected.getName(), expected.getSummary(), expected.getDescription(),
                expected.getCreditHours());
        assertThat(expected, equalTo(actual));

        // verify testRun.getObjects
        // final List<TestablePersistentObject> objects = testRun.getObjects();
        // assertTrue(objects.contains(actual));

        // delete Course
        mdao.deleteCourse(expected.getUuid(), 0);
        try {
            fdao.findCourseByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findCourseById() with unknown course.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindCourseByIdWhenCourseIsNotKnown() {
        final Integer id = 1;
        fdao.findCourseById(id);
    }

    /**
     * @test findCourseByUuid() with unknown Course.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindCourseByUuidWhenCourseIsNotKnown() {
        final String uuid = "missing";
        fdao.findCourseByUuid(uuid);
    }

    /**
     * Test updateCourse() with unknown course.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateCourseWhenCourseIsNotFound() {
        final Course course = new Course();
        course.setUuid("missing");
        mdao.updateCourse(course, "Calculus 102", "summary", "description", 2);
    }

    /**
     * Test deleteCourse() with unknown course.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteCourseWhenCourseIsNotFound() {
        mdao.deleteCourse("missing", 0);
    }
}