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

import com.invariantproperties.project.student.business.InstructorFinderService;
import com.invariantproperties.project.student.business.InstructorFinderServiceImpl;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.InstructorRepository;

/**
 * Unit tests for InstructorServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class InstructorFinderServiceImplTest {
    private final Class<Specification<Instructor>> sClass = null;

    @Test
    public void testCount() {
        final long expected = 3;

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        final long actual = service.count();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountByTestRun() {
        final long expected = 3;
        final TestRun testRun = new TestRun();

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        final long actual = service.countByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCountError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.count(any(sClass))).thenThrow(new UnitTestException());

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.count();
    }

    @Test
    public void testFindAllInstructors() {
        final List<Instructor> expected = Collections.emptyList();

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        final List<Instructor> actual = service.findAllInstructors();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllInstructorsError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.findAllInstructors();
    }

    @Test
    public void testFindInstructorById() {
        final Instructor expected = new Instructor();
        expected.setId(1);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        final Instructor actual = service.findInstructorById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindInstructorByIdMissing() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.findInstructorById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindInstructorByIdError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.findInstructorById(1);
    }

    @Test
    public void testFindInstructorByUuid() {
        final Instructor expected = new Instructor();
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        final Instructor actual = service.findInstructorByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindInstructorByUuidMissing() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(null);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.findInstructorByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindInstructorByUuidError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenThrow(new UnitTestException());

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.findInstructorByUuid("[uuid]");
    }

    @Test
    public void testFindInstructorsByTestUuid() {
        final TestRun testRun = new TestRun();
        final Instructor instructor = new Instructor();
        final List<Instructor> expected = Collections.singletonList(instructor);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        final List<Instructor> actual = service.findInstructorsByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindInstructorsByTestUuidError() {
        final TestRun testRun = new TestRun();

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final InstructorFinderService service = new InstructorFinderServiceImpl(repository);
        service.findInstructorsByTestRun(testRun);
    }
}
