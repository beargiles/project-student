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

import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_CREATE;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_CREATE_FOR_TESTING;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_DELETE;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_UPDATE;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.SectionRepository;

/**
 * Implementation of SectionService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class SectionManagerServiceImpl implements SectionManagerService {
    private static final Logger LOG = LoggerFactory
            .getLogger(SectionManagerServiceImpl.class);
    private static final String SECTION = "section";
    private static final String COULD_NOT_FIND_MESSAGE = "could not find section: ";

    @Resource
    private SectionRepository sectionRepository;

    /**
     * Default constructor
     */
    public SectionManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    SectionManagerServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      createSection(java.lang.String)
     */
    @Transactional
    @Override
    public Section createSection(String name) {
        final Section section = new Section();
        section.setName(name);

        Section actual = null;
        try {
            actual = sectionRepository.saveAndFlush(section);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE.format(SECTION);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      createSectionForTesting(java.lang.String,
     *      com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional
    @Override
    public Section createSectionForTesting(String name, TestRun testRun) {
        final Section section = new Section();
        section.setName(name);
        section.setTestRun(testRun);

        Section actual = null;
        try {
            actual = sectionRepository.saveAndFlush(section);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(SECTION);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.SectionFinderService.persistence.SectionService#
     *      updateSection(com.invariantproperties.project.section.domain.Section,
     *      java.lang.String)
     */
    @Transactional
    @Override
    public Section updateSection(Section section, String name) {
        Section updated = null;
        try {
            final Section actual = sectionRepository.findSectionByUuid(section
                    .getUuid());

            if (actual == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + section.getUuid());
                throw new ObjectNotFoundException(section.getUuid());
            }

            actual.setName(name);
            updated = sectionRepository.saveAndFlush(actual);
            section.setName(name);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_UPDATE.format(SECTION);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_UPDATE.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e);
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      deleteSection(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteSection(String uuid, Integer version) {
        Section section = null;
        try {
            section = sectionRepository.findSectionByUuid(uuid);

            if (section == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            sectionRepository.delete(section);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_DELETE.format(SECTION);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_DELETE.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e);
        }
    }
}
