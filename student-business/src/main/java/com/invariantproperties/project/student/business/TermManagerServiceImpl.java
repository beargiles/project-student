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

import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.repository.TermRepository;

/**
 * Implementation of TermService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class TermManagerServiceImpl implements TermManagerService {
    private static final Logger LOG = LoggerFactory
            .getLogger(TermManagerServiceImpl.class);
    private static final String TERM = "term";
    private static final String COULD_NOT_FIND_MESSAGE = "could not find term: ";

    @Resource
    private TermRepository termRepository;

    /**
     * Default constructor
     */
    public TermManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    TermManagerServiceImpl(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    /**
     * @see com.invariantproperties.project.term.business.TermFinderService#
     *      createTerm(java.lang.String)
     */
    @Transactional
    @Override
    public Term createTerm(String name) {
        final Term term = new Term();
        term.setName(name);

        Term actual = null;
        try {
            actual = termRepository.saveAndFlush(term);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE.format(TERM);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE.format(TERM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.term.business.TermFinderService#
     *      createTermForTesting(java.lang.String,
     *      com.invariantproperties.project.term.common.TestRun)
     */
    @Transactional
    @Override
    public Term createTermForTesting(String name, TestRun testRun) {
        final Term term = new Term();
        term.setName(name);
        term.setTestRun(testRun);

        Term actual = null;
        try {
            actual = termRepository.saveAndFlush(term);
        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(TERM);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_CREATE_FOR_TESTING.format(TERM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_CREATE_FOR_TESTING, msg, e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.project.TermFinderService.persistence.TermService#
     *      updateTerm(com.invariantproperties.project.term.domain.Term,
     *      java.lang.String)
     */
    @Transactional
    @Override
    public Term updateTerm(Term term, String name) {
        Term updated = null;
        try {
            final Term actual = termRepository.findTermByUuid(term.getUuid());

            if (actual == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + term.getUuid());
                throw new ObjectNotFoundException(term.getUuid());
            }

            actual.setName(name);
            updated = termRepository.saveAndFlush(actual);
            term.setName(name);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_UPDATE.format(TERM);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_UPDATE.format(TERM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_UPDATE, msg, e);
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.project.term.business.TermFinderService#
     *      deleteTerm(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteTerm(String uuid, Integer version) {
        Term term = null;
        try {
            term = termRepository.findTermByUuid(uuid);

            if (term == null) {
                LOG.debug(COULD_NOT_FIND_MESSAGE + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            termRepository.delete(term);

        } catch (UnitTestException e) {
            final String msg = UNABLE_TO_DELETE.format(TERM);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e);
        } catch (DataAccessException e) {
            final String msg = UNABLE_TO_DELETE.format(TERM);
            LOG.info(msg);
            throw new PersistenceException(UNABLE_TO_DELETE, msg, e);
        }
    }
}
