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

import com.invariantproperties.project.student.business.ClassroomFinderService;
import com.invariantproperties.project.student.business.ClassroomManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.webservice.config.TestRestApplicationContext1;

/**
 * Unit tests for ClassroomResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext1.class })
public class ClassroomResourceTest {
    private Classroom eng201 = new Classroom();
    private Classroom eng202 = new Classroom();

    @Resource
    private ClassroomResource resource;

    @Before
    public void init() {
        eng201.setId(1);
        eng201.setName("eng201");
        eng201.setUuid(UUID.randomUUID().toString());

        eng202.setId(2);
        eng202.setName("eng202");
        eng202.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllClassrooms() {
        final List<Classroom> expected = Arrays.asList(eng201);

        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findAllClassrooms()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllClassrooms();

        assertEquals(200, response.getStatus());
        final Classroom[] actual = (Classroom[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllClassroomsEmpty() {
        final List<Classroom> expected = new ArrayList<>();

        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findAllClassrooms()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllClassrooms();

        assertEquals(200, response.getStatus());
        final Classroom[] actual = (Classroom[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllClassroomsFailure() {
        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findAllClassrooms()).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllClassrooms();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetClassroom() {
        final Classroom expected = eng201;

        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findClassroomByUuid(expected.getUuid())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getClassroom(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Classroom actual = (Classroom) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetClassroomMissing() {
        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findClassroomByUuid(eng201.getUuid())).thenThrow(new ObjectNotFoundException(eng201.getUuid()));

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getClassroom(eng201.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetClassroomFailure() {
        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findClassroomByUuid(eng201.getUuid())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getClassroom(eng201.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateClassroom() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.createClassroom(name.getName())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createClassroom(name);

        assertEquals(201, response.getStatus());
        final Classroom actual = (Classroom) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void testCreateClassroomBlankName() {
        final NameRTO name = new NameRTO();

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.createClassroom(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createClassroom(name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the classroom can't be created for some reason. For
     * now the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateClassroomProblem() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.createClassroom(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createClassroom(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateClassroomFailure() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.createClassroom(name.getName())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createClassroom(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateClassroom() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(eng202.getName());
        final Classroom updated = new Classroom();
        updated.setId(expected.getId());
        updated.setName(eng202.getName());
        updated.setUuid(expected.getUuid());

        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(finder.findClassroomByUuid(expected.getUuid())).thenReturn(expected);
        when(manager.updateClassroom(expected, name.getName())).thenReturn(updated);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.updateClassroom(expected.getUuid(), name);

        assertEquals(200, response.getStatus());
        final Classroom actual = (Classroom) response.getEntity();
        assertNull(actual.getId());
        assertEquals(eng202.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testUpdateClassroomBlankName() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();

        final ClassroomFinderService finder = Mockito.mock(ClassroomFinderService.class);
        when(finder.findClassroomByUuid(expected.getUuid())).thenReturn(expected);

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.createClassroom(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.updateClassroom(expected.getUuid(), name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the classroom can't be updated for some reason. For
     * now the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateClassroomProblem() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.updateClassroom(expected, name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createClassroom(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateClassroomFailure() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        when(manager.updateClassroom(expected, name.getName())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createClassroom(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteClassroom() {
        final Classroom expected = eng201;

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        doNothing().when(manager).deleteClassroom(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteClassroom(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteClassroomMissing() {
        final Classroom expected = eng201;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(manager).deleteClassroom(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteClassroom(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteClassroomFailure() {
        final Classroom expected = eng201;

        final ClassroomManagerService manager = Mockito.mock(ClassroomManagerService.class);
        doThrow(new UnitTestException()).when(manager).deleteClassroom(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteClassroom(expected.getUuid(), 0);

        assertEquals(500, response.getStatus());
    }
}
