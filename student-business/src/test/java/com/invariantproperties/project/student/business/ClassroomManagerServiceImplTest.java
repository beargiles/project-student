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

import com.invariantproperties.project.student.business.ClassroomManagerService;
import com.invariantproperties.project.student.business.ClassroomManagerServiceImpl;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.repository.ClassroomRepository;

/**
 * Unit tests for ClassroomServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ClassroomManagerServiceImplTest {
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateClassroom() {
        final Classroom expected = new Classroom();
        expected.setName("name");
        expected.setUuid(UUID);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.saveAndFlush(any(Classroom.class))).thenReturn(expected);

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        final Classroom actual = service.createClassroom(expected.getName());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateClassroomError() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.saveAndFlush(any(Classroom.class))).thenThrow(new UnitTestException());

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        service.createClassroom("name");
    }

    @Test
    public void testUpdateClassroom() {
        final Classroom expected = new Classroom();
        expected.setName("Eng 201");
        expected.setUuid(UUID);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Classroom.class))).thenReturn(expected);

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        final Classroom actual = service.updateClassroom(expected, "Eng 202");

        assertEquals("Eng 202", actual.getName());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateClassroomMissing() {
        final Classroom expected = new Classroom();
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(null);

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        service.updateClassroom(expected, "Eng 202");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateClassroomError() {
        final Classroom expected = new Classroom();
        expected.setUuid(UUID);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Classroom.class));

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        service.updateClassroom(expected, "Eng 202");
    }

    @Test
    public void testDeleteClassroom() {
        final Classroom expected = new Classroom();
        expected.setUuid(UUID);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Classroom.class));

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        service.deleteClassroom(expected.getUuid(), 0);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteClassroomMissing() {
        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(null);

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        service.deleteClassroom(UUID, 0);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteClassroomError() {
        final Classroom expected = new Classroom();
        expected.setUuid(UUID);

        final ClassroomRepository repository = Mockito.mock(ClassroomRepository.class);
        when(repository.findClassroomByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Classroom.class));

        final ClassroomManagerService service = new ClassroomManagerServiceImpl(repository);
        service.deleteClassroom(expected.getUuid(), 0);
    }
}
