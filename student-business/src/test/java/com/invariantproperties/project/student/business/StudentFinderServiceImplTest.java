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

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.StudentFinderService;
import com.invariantproperties.project.student.business.StudentFinderServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.StudentRepository;

/**
 * Unit tests for StudentServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentFinderServiceImplTest {
    private final Class<Specification<Student>> sClass = null;

    @Test
    public void testCount() {
        final long expected = 3;

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        final long actual = service.count();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountByTestRun() {
        final long expected = 3;
        final TestRun testRun = new TestRun();

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        final long actual = service.countByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCountError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.count(any(sClass))).thenThrow(new UnitTestException());

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.count();
    }

    @Test
    public void testFindAllStudents() {
        final List<Student> expected = Collections.emptyList();

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        final List<Student> actual = service.findAllStudents();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllStudentsError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.findAllStudents();
    }

    @Test
    public void testFindStudentById() {
        final Student expected = new Student();
        expected.setId(1);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        final Student actual = service.findStudentById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindStudentByIdMissing() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.findStudentById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindStudentByIdError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.findStudentById(1);
    }

    @Test
    public void testFindStudentByUuid() {
        final Student expected = new Student();
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        final Student actual = service.findStudentByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindStudentByUuidMissing() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(null);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.findStudentByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindStudentByUuidError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenThrow(new UnitTestException());

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.findStudentByUuid("[uuid]");
    }

    @Test
    public void testFindStudentByTestUuid() {
        final TestRun testRun = new TestRun();
        final Student student = new Student();
        final List<Student> expected = Collections.singletonList(student);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        final List<Student> actual = service.findStudentsByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindStudentByTestUuidError() {
        final TestRun testRun = new TestRun();

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final StudentFinderService service = new StudentFinderServiceImpl(repository);
        service.findStudentsByTestRun(testRun);
    }
}
