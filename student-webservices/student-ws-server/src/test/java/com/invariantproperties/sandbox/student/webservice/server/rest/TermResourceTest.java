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
import com.invariantproperties.project.student.business.TermFinderService;
import com.invariantproperties.project.student.business.TermManagerService;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.webservice.config.TestRestApplicationContext1;

/**
 * Unit tests for TermResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext1.class })
public class TermResourceTest {
    private Term fall2013 = new Term();
    private Term fall2014 = new Term();

    @Resource
    private TermResource resource;

    @Before
    public void init() {
        fall2013.setId(1);
        fall2013.setName("Fall 2013");
        fall2013.setUuid(UUID.randomUUID().toString());

        fall2014.setId(2);
        fall2014.setName("Fall 2014");
        fall2014.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllTerms() {
        final List<Term> expected = Arrays.asList(fall2013);

        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findAllTerms()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllTerms();

        assertEquals(200, response.getStatus());
        final Term[] actual = (Term[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllTermsEmpty() {
        final List<Term> expected = new ArrayList<>();

        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findAllTerms()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllTerms();

        assertEquals(200, response.getStatus());
        final Term[] actual = (Term[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllTermsFailure() {
        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findAllTerms()).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.findAllTerms();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetTerm() {
        final Term expected = fall2013;

        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findTermByUuid(expected.getUuid())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getTerm(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Term actual = (Term) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetTermMissing() {
        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findTermByUuid(fall2013.getUuid())).thenThrow(new ObjectNotFoundException(fall2013.getUuid()));

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getTerm(fall2013.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetTermFailure() {
        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findTermByUuid(fall2013.getUuid())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, null, testService);
        final Response response = resource.getTerm(fall2013.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateTerm() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.createTerm(name.getName())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createTerm(name);

        assertEquals(201, response.getStatus());
        final Term actual = (Term) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void testCreateTermBlankName() {
        final NameRTO name = new NameRTO();

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.createTerm(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createTerm(name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the term can't be created for some reason. For now the
     * manager layer just returns a null value - it should throw an appropriate
     * exception.
     */
    @Test
    public void testCreateTermProblem() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.createTerm(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createTerm(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateTermFailure() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.createTerm(name.getName())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createTerm(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateTerm() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(fall2014.getName());
        final Term updated = new Term();
        updated.setId(expected.getId());
        updated.setName(fall2014.getName());
        updated.setUuid(expected.getUuid());

        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(finder.findTermByUuid(expected.getUuid())).thenReturn(expected);
        when(manager.updateTerm(expected, name.getName())).thenReturn(updated);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(finder, manager, testService);
        final Response response = resource.updateTerm(expected.getUuid(), name);

        assertEquals(200, response.getStatus());
        final Term actual = (Term) response.getEntity();
        assertNull(actual.getId());
        assertEquals(fall2014.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testUpdateTermBlankName() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.createTerm(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.updateTerm(expected.getUuid(), name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the term can't be updated for some reason. For now the
     * manager layer just returns a null value - it should throw an appropriate
     * exception.
     */
    @Test
    public void testUpdateTermProblem() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.updateTerm(expected, name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createTerm(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateTermFailure() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        when(manager.updateTerm(expected, name.getName())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.createTerm(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteTerm() {
        final Term expected = fall2013;

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        doNothing().when(manager).deleteTerm(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteTerm(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteTermMissing() {
        final Term expected = fall2013;
        final NameRTO name = new NameRTO();
        name.setName(expected.getName());

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(manager).deleteTerm(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteTerm(expected.getUuid(), 0);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteTermFailure() {
        final Term expected = fall2013;

        final TermFinderService finder = Mockito.mock(TermFinderService.class);
        when(finder.findTermByUuid(expected.getUuid())).thenReturn(expected);

        final TermManagerService manager = Mockito.mock(TermManagerService.class);
        doThrow(new UnitTestException()).when(manager).deleteTerm(expected.getUuid(), 0);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        resource.setServices(null, manager, testService);
        final Response response = resource.deleteTerm(expected.getUuid(), 0);

        assertEquals(500, response.getStatus());
    }
}
