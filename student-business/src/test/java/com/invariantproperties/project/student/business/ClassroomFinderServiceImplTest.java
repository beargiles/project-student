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

import com.invariantproperties.project.student.business.ClassroomFinderService;
import com.invariantproperties.project.student.business.ClassroomFinderServiceImpl;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.ClassroomRepository;

/**
 * Unit tests for ClassroomServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ClassroomFinderServiceImplTest {
    private final Class<Specification<Classroom>> sClass = null;

    @Test
    public void testCount() {
        final long expected = 3;

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        final long actual = service.count();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountByTestRun() {
        final long expected = 3;
        final TestRun testRun = new TestRun();

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        final long actual = service.countByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCountError() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.count(any(sClass))).thenThrow(new UnitTestException());

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.count();
    }

    @Test
    public void testFindAllClassrooms() {
        final List<Classroom> expected = Collections.emptyList();

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        final List<Classroom> actual = service.findAllClassrooms();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllClassroomsError() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.findAllClassrooms();
    }

    @Test
    public void testFindClassroomById() {
        final Classroom expected = new Classroom();
        expected.setId(1);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        final Classroom actual = service.findClassroomById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindClassroomByIdMissing() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.findClassroomById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindClassroomByIdError() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.findClassroomById(1);
    }

    @Test
    public void testFindClassroomByUuid() {
        final Classroom expected = new Classroom();
        expected.setUuid("[uuid]");

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(expected);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        final Classroom actual = service.findClassroomByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindClassroomByUuidMissing() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(null);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.findClassroomByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindClassroomByUuidError() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenThrow(new UnitTestException());

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.findClassroomByUuid("[uuid]");
    }

    @Test
    public void testFindClassroomsByTestUuid() {
        final TestRun testRun = new TestRun();
        final Classroom classroom = new Classroom();
        final List<Classroom> expected = Collections.singletonList(classroom);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        final List<Classroom> actual = service.findClassroomsByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindClassroomsByTestUuidError() {
        final TestRun testRun = new TestRun();

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final ClassroomFinderService service = new ClassroomFinderServiceImpl(repository);
        service.findClassroomsByTestRun(testRun);
    }
}
