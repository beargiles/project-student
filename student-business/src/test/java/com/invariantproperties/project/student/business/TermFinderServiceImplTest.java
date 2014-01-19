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
import com.invariantproperties.project.student.business.TermFinderService;
import com.invariantproperties.project.student.business.TermFinderServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.TermRepository;

// import static com.invariantproperties.project.student.specification.TermSpecifications.*;

/**
 * Unit tests for TermServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TermFinderServiceImplTest {
    private final Class<Specification<Term>> sClass = null;

    @Test
    public void testCount() {
        final long expected = 3;

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        final long actual = service.count();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountByTestRun() {
        final long expected = 3;
        final TestRun testRun = new TestRun();

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        final long actual = service.countByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCountError() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.count(any(sClass))).thenThrow(new UnitTestException());

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.count();
    }

    @Test
    public void testFindAllTerms() {
        final List<Term> expected = Collections.emptyList();

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        final List<Term> actual = service.findAllTerms();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllTermsError() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.findAllTerms();
    }

    @Test
    public void testFindTermById() {
        final Term expected = new Term();
        expected.setId(1);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        final Term actual = service.findTermById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindTermByIdMissing() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.findTermById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindTermByIdError() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.findTermById(1);
    }

    @Test
    public void testFindTermByUuid() {
        final Term expected = new Term();
        expected.setUuid("[uuid]");

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(expected);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        final Term actual = service.findTermByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindTermByUuidMissing() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenReturn(null);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.findTermByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindTermByUuidError() {
        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findTermByUuid(any(String.class))).thenThrow(new UnitTestException());

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.findTermByUuid("[uuid]");
    }

    @Test
    public void testFindTermsByTestUuid() {
        final TestRun testRun = new TestRun();
        final Term term = new Term();
        final List<Term> expected = Collections.singletonList(term);

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final TermFinderService service = new TermFinderServiceImpl(repository);
        final List<Term> actual = service.findTermsByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindTermsByTestUuidError() {
        final TestRun testRun = new TestRun();

        final TermRepository repository = Mockito.mock(TermRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final TermFinderService service = new TermFinderServiceImpl(repository);
        service.findTermsByTestRun(testRun);
    }
}
