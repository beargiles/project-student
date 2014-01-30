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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.webservice.config.TestRestApplicationContext1;

/**
 * Unit tests for CourseResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext1.class })
public class CourseResourceTest {
    private Course physics = new Course();
    private Course mechanics = new Course();

    @Resource
    private CourseResource resource;

    @Before
    public void init() {
        physics.setId(1);
        physics.setCode("PHYS101");
        physics.setName("physics");
        physics.setSummary("summary 1");
        physics.setDescription("description 1");
        physics.setCreditHours(1);
        physics.setUuid(UUID.randomUUID().toString());

        mechanics.setId(2);
        mechanics.setCode("PHYS201");
        mechanics.setName("mechanics");
        mechanics.setSummary("summary 2");
        mechanics.setDescription("description 2");
        mechanics.setCreditHours(2);
        mechanics.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllCourses() {
        final List<Course> expected = Arrays.asList(physics);

        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findAllCourses()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllCourses();

        assertEquals(200, response.getStatus());
        final Course[] actual = (Course[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        actual[0].setId(expected.get(0).getId());
        actual[0].setUuid(expected.get(0).getUuid());
        actual[0].setCreationDate(expected.get(0).getCreationDate());
        assertThat(expected.get(0), equalTo(actual[0]));
    }

    @Test
    public void testFindAllCoursesEmpty() {
        final List<Course> expected = new ArrayList<>();

        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findAllCourses()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllCourses();

        assertEquals(200, response.getStatus());
        final Course[] actual = (Course[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllCoursesFailure() {
        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findAllCourses()).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllCourses();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetCourse() {
        final Course expected = physics;

        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findCourseByUuid(expected.getUuid())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getCourse(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Course actual = (Course) response.getEntity();
        actual.setId(expected.getId());
        actual.setUuid(expected.getUuid());
        actual.setCreationDate(expected.getCreationDate());
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testGetCourseMissing() {
        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findCourseByUuid(physics.getUuid())).thenThrow(new ObjectNotFoundException(physics.getUuid()));

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getCourse(physics.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetCourseFailure() {
        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findCourseByUuid(physics.getUuid())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getCourse(physics.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateCourse() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(expected);

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(
                manager.createCourse(expected.getCode(), expected.getName(), expected.getSummary(),
                        expected.getDescription(), expected.getCreditHours())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createCourse(info);

        assertEquals(201, response.getStatus());
        final Course actual = (Course) response.getEntity();
        actual.setId(expected.getId());
        actual.setUuid(expected.getUuid());
        actual.setCreationDate(expected.getCreationDate());
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testCreateCourseBlankName() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(expected);
        info.setName("");

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(
                manager.createCourse(expected.getCode(), expected.getName(), expected.getSummary(),
                        expected.getDescription(), expected.getCreditHours())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createCourse(info);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the course can't be created for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateCourseProblem() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(expected);

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(
                manager.createCourse(expected.getCode(), expected.getName(), expected.getSummary(),
                        expected.getDescription(), expected.getCreditHours())).thenReturn(null);
        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createCourse(info);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateCourseFailure() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(expected);

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(
                manager.createCourse(expected.getCode(), expected.getName(), expected.getSummary(),
                        expected.getDescription(), expected.getCreditHours())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createCourse(info);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateCourse() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(mechanics);
        final Course updated = new Course();
        updated.setId(expected.getId());
        updated.setCode(mechanics.getCode());
        updated.setName(mechanics.getName());
        updated.setSummary(mechanics.getSummary());
        updated.setDescription(mechanics.getDescription());
        updated.setCreditHours(mechanics.getCreditHours());
        updated.setUuid(expected.getUuid());

        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(finder.findCourseByUuid(expected.getUuid())).thenReturn(expected);
        when(
                manager.updateCourse(expected, updated.getName(), updated.getSummary(), updated.getDescription(),
                        updated.getCreditHours())).thenReturn(updated);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.updateCourse(expected.getUuid(), info);

        assertEquals(200, response.getStatus());
        final Course actual = (Course) response.getEntity();
        actual.setId(updated.getId());
        actual.setUuid(updated.getUuid());
        actual.setCreationDate(updated.getCreationDate());
        assertThat(updated, equalTo(actual));
    }

    @Test
    public void testUpdateCourseBlankName() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO();

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(
                manager.createCourse(expected.getCode(), expected.getName(), expected.getSummary(),
                        expected.getDescription(), 1)).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.updateCourse(expected.getUuid(), info);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the course can't be updated for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateCourseProblem() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(expected);

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(manager.updateCourse(expected, expected.getName(), expected.getSummary(), expected.getDescription(), 1))
                .thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createCourse(info);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateCourseFailure() {
        final Course expected = physics;
        final CourseInfoRTO info = new CourseInfoRTO(expected);

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        when(manager.updateCourse(expected, expected.getName(), expected.getSummary(), expected.getDescription(), 1))
                .thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createCourse(info);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteCourse() {
        final Course expected = physics;

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        doNothing().when(manager).deleteCourse(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteCourse(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteCourseMissing() {
        final Course expected = physics;

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(manager).deleteCourse(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteCourse(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteCourseFailure() {
        final Course expected = physics;

        final CourseFinderService finder = Mockito.mock(CourseFinderService.class);
        when(finder.findCourseByUuid(expected.getUuid())).thenReturn(expected);

        final CourseManagerService manager = Mockito.mock(CourseManagerService.class);
        doThrow(new UnitTestException()).when(manager).deleteCourse(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.deleteCourse(expected.getUuid(), 0);

        assertEquals(500, response.getStatus());
    }
}
