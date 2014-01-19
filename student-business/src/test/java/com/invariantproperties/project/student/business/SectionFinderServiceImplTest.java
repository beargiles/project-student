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
import com.invariantproperties.project.student.business.SectionFinderService;
import com.invariantproperties.project.student.business.SectionFinderServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.SectionRepository;

/**
 * Unit tests for SectionServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionFinderServiceImplTest {
    private final Class<Specification<Section>> sClass = null;

    @Test
    public void testCount() {
        final long expected = 3;

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        final long actual = service.count();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountByTestRun() {
        final long expected = 3;
        final TestRun testRun = new TestRun();

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.count(any(sClass))).thenReturn(expected);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        final long actual = service.countByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCountError() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.count(any(sClass))).thenThrow(new UnitTestException());

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.count();
    }

    @Test
    public void testFindAllSections() {
        final List<Section> expected = Collections.emptyList();

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        final List<Section> actual = service.findAllSections();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllSectionsError() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.findAllSections();
    }

    @Test
    public void testFindSectionById() {
        final Section expected = new Section();
        expected.setId(1);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        final Section actual = service.findSectionById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindSectionByIdMissing() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.findSectionById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindSectionByIdError() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.findSectionById(1);
    }

    @Test
    public void testFindSectionByUuid() {
        final Section expected = new Section();
        expected.setUuid("[uuid]");

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(expected);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        final Section actual = service.findSectionByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindSectionByUuidMissing() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(null);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.findSectionByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindSectionByUuidError() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenThrow(new UnitTestException());

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.findSectionByUuid("[uuid]");
    }

    @Test
    public void testFindSectionByTestUuid() {
        final TestRun testRun = new TestRun();
        final Section section = new Section();
        final List<Section> expected = Collections.singletonList(section);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findAll(any(sClass))).thenReturn(expected);

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        final List<Section> actual = service.findSectionsByTestRun(testRun);

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindSectionByTestUuidError() {
        final TestRun testRun = new TestRun();

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findAll(any(sClass))).thenThrow(new UnitTestException());

        final SectionFinderService service = new SectionFinderServiceImpl(repository);
        service.findSectionsByTestRun(testRun);
    }
}
