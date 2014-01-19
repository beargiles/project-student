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

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.StudentManagerService;
import com.invariantproperties.project.student.business.StudentManagerServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.repository.StudentRepository;

/**
 * Unit tests for StudentServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentManagerServiceImplTest {
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateStudent() {
        final Student expected = new Student();
        expected.setName("name");
        expected.setEmailAddress("email");
        expected.setUuid(UUID);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.saveAndFlush(any(Student.class))).thenReturn(expected);

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        final Student actual = service.createStudent(expected.getName(), expected.getEmailAddress());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateStudentError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.saveAndFlush(any(Student.class))).thenThrow(new UnitTestException());

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        service.createStudent("name", "email");
    }

    @Test
    public void testUpdateStudent() {
        final Student expected = new Student();
        expected.setName("Alice");
        expected.setName("alice@example.com");
        expected.setUuid(UUID);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Student.class))).thenReturn(expected);

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        final Student actual = service.updateStudent(expected, "Bob", "bob@example.com");

        assertEquals("Bob", actual.getName());
        assertEquals("bob@example.com", actual.getEmailAddress());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateStudentMissing() {
        final Student expected = new Student();
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(null);

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        service.updateStudent(expected, "Bob", "bob@example.com");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateStudentError() {
        final Student expected = new Student();
        expected.setUuid(UUID);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Student.class));

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        service.updateStudent(expected, "Bob", "bob@example.com");
    }

    @Test
    public void testDeleteStudent() {
        final Student expected = new Student();
        expected.setUuid(UUID);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Student.class));

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        service.deleteStudent(expected.getUuid(), 0);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteStudentMissing() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(null);

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        service.deleteStudent(UUID, 0);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteStudentError() {
        final Student expected = new Student();
        expected.setUuid(UUID);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Student.class));

        final StudentManagerService service = new StudentManagerServiceImpl(repository);
        service.deleteStudent(expected.getUuid(), 0);
    }
}
