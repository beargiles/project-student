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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.StudentFinderService;
import com.invariantproperties.project.student.business.StudentManagerService;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.webservice.config.TestRestApplicationContext1;

/**
 * Unit tests for StudentResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext1.class })
public class StudentResourceTest {
    private Student david = new Student();
    private Student edith = new Student();

    @Resource
    private StudentResource resource;

    @Before
    public void init() {
        david.setId(1);
        david.setName("David");
        david.setEmailAddress("david@example.com");
        david.setUuid(UUID.randomUUID().toString());

        edith.setId(2);
        edith.setName("Edith");
        edith.setEmailAddress("edith@example.com");
        edith.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllStudents() {
        final List<Student> expected = Arrays.asList(david);

        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findAllStudents()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllStudents();

        assertEquals(200, response.getStatus());
        final Student[] actual = (Student[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getEmailAddress(), actual[0].getEmailAddress());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllStudentsEmpty() {
        final List<Student> expected = new ArrayList<>();

        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findAllStudents()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllStudents();

        assertEquals(200, response.getStatus());
        final Student[] actual = (Student[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllStudentsFailure() {
        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findAllStudents()).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllStudents();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetStudent() {
        final Student expected = david;

        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findStudentByUuid(expected.getUuid())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getStudent(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Student actual = (Student) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmailAddress(), actual.getEmailAddress());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetStudentMissing() {
        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findStudentByUuid(david.getUuid())).thenThrow(new ObjectNotFoundException(david.getUuid()));

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getStudent(david.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetStudentFailure() {
        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findStudentByUuid(david.getUuid())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getStudent(david.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateStudent() {
        final Student expected = david;
        final NameAndEmailAddressRTO req = new NameAndEmailAddressRTO();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        when(manager.createStudent(req.getName(), req.getEmailAddress())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createStudent(req);

        assertEquals(201, response.getStatus());
        final Student actual = (Student) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmailAddress(), actual.getEmailAddress());
    }

    /**
     * Test handling when the student can't be created for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateStudentProblem() {
        final Student expected = david;
        final NameAndEmailAddressRTO req = new NameAndEmailAddressRTO();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        when(manager.createStudent(req.getName(), req.getEmailAddress())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateStudentFailure() {
        final Student expected = david;
        final NameAndEmailAddressRTO req = new NameAndEmailAddressRTO();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        when(manager.createStudent(req.getName(), req.getEmailAddress())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateStudent() {
        final Student expected = david;
        final NameAndEmailAddressRTO req = new NameAndEmailAddressRTO();
        req.setName(edith.getName());
        req.setEmailAddress(edith.getEmailAddress());
        final Student updated = new Student();
        updated.setId(expected.getId());
        updated.setName(edith.getName());
        updated.setEmailAddress(edith.getEmailAddress());
        updated.setUuid(expected.getUuid());

        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        when(finder.findStudentByUuid(expected.getUuid())).thenReturn(expected);
        when(manager.updateStudent(expected, req.getName(), req.getEmailAddress())).thenReturn(updated);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.updateStudent(expected.getUuid(), req);

        assertEquals(200, response.getStatus());
        final Student actual = (Student) response.getEntity();
        assertNull(actual.getId());
        assertEquals(edith.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    /**
     * Test handling when the student can't be updated for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateStudentProblem() {
        final Student expected = david;
        final NameAndEmailAddressRTO req = new NameAndEmailAddressRTO();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        when(manager.updateStudent(expected, req.getName(), req.getEmailAddress())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateStudentFailure() {
        final Student expected = david;
        final NameAndEmailAddressRTO req = new NameAndEmailAddressRTO();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        when(manager.updateStudent(expected, req.getName(), req.getEmailAddress())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteStudent() {
        final Student expected = david;

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        doNothing().when(manager).deleteStudent(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteStudent(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteStudentMissing() {
        final Student expected = david;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(manager).deleteStudent(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteStudent(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteStudentFailure() {
        final Student expected = david;

        final StudentFinderService finder = Mockito.mock(StudentFinderService.class);
        when(finder.findStudentByUuid(expected.getUuid())).thenReturn(expected);

        final StudentManagerService manager = Mockito.mock(StudentManagerService.class);
        doThrow(new UnitTestException()).when(manager).deleteStudent(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.deleteStudent(expected.getUuid(), 0);

        assertEquals(500, response.getStatus());
    }
}
