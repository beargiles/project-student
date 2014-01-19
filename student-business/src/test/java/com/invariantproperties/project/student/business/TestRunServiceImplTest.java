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
 * specific language governing pestRunissions and limitations
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
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.business.TestRunServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.TestRunRepository;

/**
 * Unit tests for TestRunServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TestRunServiceImplTest {
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateTestRun() {
        final TestRun expected = new TestRun();
        expected.setUuid(UUID);

        final TestRunRepository repository = Mockito.mock(TestRunRepository.class);
        when(repository.saveAndFlush(any(TestRun.class))).thenReturn(expected);

        final TestRunService service = new TestRunServiceImpl(repository);
        final TestRun actual = service.createTestRun(expected.getName());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateTestRunError() {
        final TestRunRepository repository = Mockito.mock(TestRunRepository.class);
        when(repository.saveAndFlush(any(TestRun.class))).thenThrow(new UnitTestException());

        final TestRunService service = new TestRunServiceImpl(repository);
        service.createTestRun();
    }

    @Test
    public void testDeleteTestRun() {
        final TestRun expected = new TestRun();
        expected.setUuid(UUID);

        final TestRunRepository repository = Mockito.mock(TestRunRepository.class);
        when(repository.findTestRunByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(TestRun.class));

        final TestRunService service = new TestRunServiceImpl(repository);
        service.deleteTestRun(expected.getUuid());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTestRunMissing() {
        final TestRunRepository repository = Mockito.mock(TestRunRepository.class);
        when(repository.findTestRunByUuid(any(String.class))).thenReturn(null);

        final TestRunService service = new TestRunServiceImpl(repository);
        service.deleteTestRun(UUID);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteTestRunError() {
        final TestRun expected = new TestRun();
        expected.setUuid(UUID);

        final TestRunRepository repository = Mockito.mock(TestRunRepository.class);
        when(repository.findTestRunByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(TestRun.class));

        final TestRunService service = new TestRunServiceImpl(repository);
        service.deleteTestRun(expected.getUuid());
    }
}
