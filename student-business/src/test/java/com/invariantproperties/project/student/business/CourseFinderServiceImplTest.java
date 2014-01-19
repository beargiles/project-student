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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseFinderServiceImpl;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.CourseRepository;

/**
 * Unit tests for CourseServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseFinderServiceImplTest {
    private final Class<Specification<Course>> sClass = null;

    @Test
    public void testCount() {
        final long expected = 3;

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final long actual = service.count();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountByTestRun() {
        final long expected = 3;
        final TestRun testRun = new TestRun();

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final long actual = service.countByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCountError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.count(any(sClass))).thenThrow(new UnitTestException());

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.count();
    }

    @Test
    public void testFindAllCourses() {
        final List<Course> expected = Collections.emptyList();

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final List<Course> actual = service.findAllCourses();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllCoursesError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findAllCourses();
    }

    @Test
    public void testFindCourseById() {
        final Course expected = new Course();
        expected.setId(1);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final Course actual = service.findCourseById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindCourseByIdMissing() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findCourseById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindCourseByIdError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findCourseById(1);
    }

    @Test
    public void testFindCourseByUuid() {
        final Course expected = new Course();
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final Course actual = service.findCourseByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindCourseByUuidMissing() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(null);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findCourseByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindCourseByUuidError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenThrow(new UnitTestException());

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findCourseByUuid("[uuid]");
    }

    @Test
    public void testFindCourseByTestUuid() {
        final TestRun testRun = new TestRun();
        final Course course = new Course();
        final List<Course> expected = Collections.singletonList(course);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final List<Course> actual = service.findCoursesByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindCourseByTestUuidError() {
        final TestRun testRun = new TestRun();

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findCoursesByTestRun(testRun);
    }

    @Test
    public void testFindCoursesByTestUuid() {
        final TestRun testRun = new TestRun();
        final Course course = new Course();
        final List<Course> expected = Collections.singletonList(course);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        final List<Course> actual = service.findCoursesByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindCoursesByTestUuidError() {
        final TestRun testRun = new TestRun();

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final CourseFinderService service = new CourseFinderServiceImpl(repository);
        service.findCoursesByTestRun(testRun);
    }
}
