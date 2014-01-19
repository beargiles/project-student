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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.business.CourseManagerServiceImpl;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.repository.CourseRepository;

/**
 * Unit tests for CourseServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseManagerServiceImplTest {
    private static final String CODE1 = "code 1";
    private static final String NAME1 = "name 1";
    private static final String NAME2 = "name 2";
    private static final String SUMMARY1 = "summary 1";
    private static final String SUMMARY2 = "summary 2";
    private static final String DESCRIPTION1 = "description 1";
    private static final String DESCRIPTION2 = "description 2";
    private static final Integer HOURS1 = 1;
    private static final Integer HOURS2 = 2;
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateCourse() {
        final Course expected = new Course();
        expected.setCode(CODE1);
        expected.setName(NAME1);
        expected.setSummary(SUMMARY1);
        expected.setDescription(DESCRIPTION1);
        expected.setCreditHours(HOURS1);
        expected.setUuid(UUID);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.saveAndFlush(any(Course.class))).thenReturn(expected);

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        final Course actual = service.createCourse(CODE1, NAME1, SUMMARY1, DESCRIPTION1, HOURS1);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateCourseError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.saveAndFlush(any(Course.class))).thenThrow(new UnitTestException());

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        service.createCourse(CODE1, NAME1, SUMMARY1, DESCRIPTION1, HOURS1);
    }

    @Test
    public void testUpdateCourse() {
        final Course expected = new Course();
        expected.setCode(CODE1);
        expected.setName(NAME1);
        expected.setSummary(SUMMARY1);
        expected.setDescription(DESCRIPTION1);
        expected.setCreditHours(HOURS1);
        expected.setUuid(UUID);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Course.class))).thenReturn(expected);

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        final Course actual = service.updateCourse(expected, NAME2, SUMMARY2, DESCRIPTION2, HOURS2);

        assertEquals(NAME2, actual.getName());
        assertEquals(SUMMARY2, actual.getSummary());
        assertEquals(DESCRIPTION2, actual.getDescription());
        assertEquals(HOURS2, actual.getCreditHours());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateCourseMissing() {
        final Course expected = new Course();
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(null);

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        service.updateCourse(expected, NAME2, SUMMARY2, DESCRIPTION2, HOURS2);
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateCourseError() {
        final Course expected = new Course();
        expected.setUuid(UUID);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Course.class));

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        service.updateCourse(expected, NAME2, SUMMARY2, DESCRIPTION2, HOURS2);
    }

    @Test
    public void testDeleteCourse() {
        final Course expected = new Course();
        expected.setUuid(UUID);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Course.class));

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        service.deleteCourse(expected.getUuid(), 0);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteCourseMissing() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(null);

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        service.deleteCourse(UUID, 0);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteCourseError() {
        final Course expected = new Course();
        expected.setUuid(UUID);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Course.class));

        final CourseManagerService service = new CourseManagerServiceImpl(repository);
        service.deleteCourse(expected.getUuid(), 0);
    }
}
