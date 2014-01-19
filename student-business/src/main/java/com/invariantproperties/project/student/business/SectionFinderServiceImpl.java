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

import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_COUNT;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_ID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_FIND_BY_UUID;
import static com.invariantproperties.project.student.business.PersistenceException.Type.UNABLE_TO_LIST;
import static com.invariantproperties.project.student.specification.SectionSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.PersistenceException;
import com.invariantproperties.project.student.business.SectionFinderService;
import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.SectionRepository;

/**
 * Implementation of SectionService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class SectionFinderServiceImpl implements SectionFinderService {
    private static final Logger LOG = LoggerFactory.getLogger(SectionFinderServiceImpl.class);
    private static final String SECTION = "section";
    private static final String SECTIONS = "sections";

    @Resource
    private SectionRepository sectionRepository;

    /**
     * Default constructor
     */
    public SectionFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    SectionFinderServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    /**
     * @see com.invariantproperties.project.student.business.FinderService#
     *      count()
     */
    @Transactional(readOnly = true)
    @Override
    public long count() {
        return countByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.FinderService#
     *      countByTestRun(com.invariantproperties.project.student.domain.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public long countByTestRun(TestRun testRun) {
        long count = 0;
        try {
            count = sectionRepository.count(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_COUNT.format(SECTIONS) + testRun;
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_COUNT.format(SECTIONS) + testRun;
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_COUNT, msg, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      findAllSections()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Section> findAllSections() {
        return findSectionsByTestRun(null);
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      findSectionById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Section findSectionById(Integer id) {
        Section section = null;
        try {
            section = sectionRepository.findOne(id);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(SECTION);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_ID.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_ID, msg, e, id);
        }

        if (section == null) {
            throw new ObjectNotFoundException(id);
        }

        return section;
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      findSectionByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Section findSectionByUuid(String uuid) {
        Section section = null;
        try {
            section = sectionRepository.findSectionByUuid(uuid);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(SECTION);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_FIND_BY_UUID.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_FIND_BY_UUID, msg, e, uuid);
        }

        if (section == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return section;
    }

    /**
     * @see com.invariantproperties.project.student.business.SectionFinderService#
     *      findSectionsByTestRun(com.invariantproperties.project.student.common.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Section> findSectionsByTestRun(TestRun testRun) {
        List<Section> sections = null;

        try {
            sections = sectionRepository.findAll(testRunIs(testRun));
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_LIST.format(SECTION);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_LIST.format(SECTION);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_LIST, msg, e);
        }

        return sections;
    }
}
