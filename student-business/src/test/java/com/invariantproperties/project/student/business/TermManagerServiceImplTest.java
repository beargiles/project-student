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
import com.invariantproperties.project.student.business.TermManagerService;
import com.invariantproperties.project.student.business.TermManagerServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.repository.TermRepository;

/**
 * Unit tests for TermServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TermManagerServiceImplTest {
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateTerm() {
        final Term expected = new Term();
        expected.setName("name");
        expected.setUuid(UUID);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.saveAndFlush(any(Term.class))).thenReturn(expected);

        final TermManagerService service = new TermManagerServiceImpl(repository);
        final Term actual = service.createTerm(expected.getName());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateTermError() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.saveAndFlush(any(Term.class))).thenThrow(new UnitTestException());

        final TermManagerService service = new TermManagerServiceImpl(repository);
        service.createTerm("name");
    }

    @Test
    public void testUpdateTerm() {
        final Term expected = new Term();
        expected.setName("Fall 2013");
        expected.setUuid(UUID);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Term.class))).thenReturn(expected);

        final TermManagerService service = new TermManagerServiceImpl(repository);
        final Term actual = service.updateTerm(expected, "Fall 2014");

        assertEquals("Fall 2014", actual.getName());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateTermMissing() {
        final Term expected = new Term();
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(null);

        final TermManagerService service = new TermManagerServiceImpl(repository);
        service.updateTerm(expected, "Physics - Fall 2014");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateTermError() {
        final Term expected = new Term();
        expected.setUuid(UUID);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Term.class));

        final TermManagerService service = new TermManagerServiceImpl(repository);
        service.updateTerm(expected, "Physics - Fall 2014");
    }

    @Test
    public void testDeleteTerm() {
        final Term expected = new Term();
        expected.setUuid(UUID);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Term.class));

        final TermManagerService service = new TermManagerServiceImpl(repository);
        service.deleteTerm(expected.getUuid(), 0);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTermMissing() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(null);

        final TermManagerService service = new TermManagerServiceImpl(repository);
        service.deleteTerm(UUID, 0);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteTermError() {
        final Term expected = new Term();
        expected.setUuid(UUID);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Term.class));

        final TermManagerService service = new TermManagerServiceImpl(repository);
        service.deleteTerm(expected.getUuid(), 0);
    }
}
