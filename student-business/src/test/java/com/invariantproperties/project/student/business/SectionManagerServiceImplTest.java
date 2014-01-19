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
import com.invariantproperties.project.student.business.SectionManagerService;
import com.invariantproperties.project.student.business.SectionManagerServiceImpl;
import com.invariantproperties.project.student.business.UnitTestException;
import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.repository.SectionRepository;

/**
 * Unit tests for SectionServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionManagerServiceImplTest {
    private static final String UUID = "11111111-1111-1111-1111-111111111111";

    @Test
    public void testCreateSection() {
        final Section expected = new Section();
        expected.setName("name");
        expected.setUuid(UUID);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.saveAndFlush(any(Section.class))).thenReturn(expected);

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        final Section actual = service.createSection(expected.getName());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateSectionError() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.saveAndFlush(any(Section.class))).thenThrow(new UnitTestException());

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        service.createSection("name");
    }

    @Test
    public void testUpdateSection() {
        final Section expected = new Section();
        expected.setName("Physics - Fall 2013");
        expected.setUuid(UUID);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Section.class))).thenReturn(expected);

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        final Section actual = service.updateSection(expected, "Physics - Fall 2014");

        assertEquals("Physics - Fall 2014", actual.getName());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateSectionMissing() {
        final Section expected = new Section();
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(null);

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        service.updateSection(expected, "Physics - Fall 2014");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateSectionError() {
        final Section expected = new Section();
        expected.setUuid(UUID);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Section.class));

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        service.updateSection(expected, "Physics - Fall 2014");
    }

    @Test
    public void testDeleteSection() {
        final Section expected = new Section();
        expected.setUuid(UUID);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Section.class));

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        service.deleteSection(expected.getUuid(), 0);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteSectionMissing() {
        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(null);

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        service.deleteSection(UUID, 0);
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteSectionError() {
        final Section expected = new Section();
        expected.setUuid(UUID);

        final SectionRepository repository = Mockito.mock(SectionRepository.class);
        when(repository.findSectionByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Section.class));

        final SectionManagerService service = new SectionManagerServiceImpl(repository);
        service.deleteSection(expected.getUuid(), 0);
    }
}
