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

import com.invariantproperties.project.student.business.InstructorManagerService;
import com.invariantproperties.project.student.business.InstructorManagerServiceImpl;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.repository.InstructorRepository;

/**
 * Unit tests for InstructorServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class InstructorManagerServiceImplTest {
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateInstructor() {
        final Instructor expected = new Instructor();
        expected.setName("name");
        expected.setEmailAddress("email");
        expected.setUuid(UUID);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.saveAndFlush(any(Instructor.class))).thenReturn(expected);

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        final Instructor actual = service.createInstructor(expected.getName(), expected.getEmailAddress());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateInstructorError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.saveAndFlush(any(Instructor.class))).thenThrow(new UnitTestException());

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        service.createInstructor("name", "email");
    }

    @Test
    public void testUpdateInstructor() {
        final Instructor expected = new Instructor();
        expected.setName("Alice");
        expected.setName("alice@example.com");
        expected.setUuid(UUID);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Instructor.class))).thenReturn(expected);

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        final Instructor actual = service.updateInstructor(expected, "Bob", "bob@example.com");

        assertEquals("Bob", actual.getName());
        assertEquals("bob@example.com", actual.getEmailAddress());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateInstructorMissing() {
        final Instructor expected = new Instructor();
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(null);

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        service.updateInstructor(expected, "Bob", "bob@example.com");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateInstructorError() {
        final Instructor expected = new Instructor();
        expected.setUuid(UUID);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Instructor.class));

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        service.updateInstructor(expected, "Bob", "bob@example.com");
    }

    @Test
    public void testDeleteInstructor() {
        final Instructor expected = new Instructor();
        expected.setUuid(UUID);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Instructor.class));

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        service.deleteInstructor(expected.getUuid(), 0);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteInstructorMissing() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(null);

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        service.deleteInstructor(UUID, 0);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteInstructorError() {
        final Instructor expected = new Instructor();
        expected.setUuid(UUID);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Instructor.class));

        final InstructorManagerService service = new InstructorManagerServiceImpl(repository);
        service.deleteInstructor(expected.getUuid(), 0);
    }
}
