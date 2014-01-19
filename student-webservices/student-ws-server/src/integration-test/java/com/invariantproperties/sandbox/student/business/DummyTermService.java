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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;

public class DummyTermService implements TermFinderService, TermManagerService {
    private static final Logger log = Logger.getLogger(DummyTermService.class);
    private Map<String, Term> cache = Collections.synchronizedMap(new HashMap<String, Term>());

    @Override
    public long count() {
        log.debug("TermServer: count()");
        return countByTestRun(null);
    }

    @Override
    public long countByTestRun(TestRun testRun) {
        log.debug("TermServer: countByTestRun()");
        long count = 0;
        for (Term term : cache.values()) {
            if (testRun.equals(term.getTestRun())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<Term> findAllTerms() {
        log.debug("TermServer: findAllTerms()");
        return new ArrayList<Term>(cache.values());
    }

    @Override
    public Term findTermById(Integer id) {
        throw new ObjectNotFoundException(id);
    }

    @Override
    public Term findTermByUuid(String uuid) {
        log.debug("TermServer: findTermByUuid()");
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    @Override
    public List<Term> findTermsByTestRun(TestRun testRun) {
        log.debug("TermServer: findTermByTestRun()");
        final List<Term> results = new ArrayList<Term>();
        for (Term term : cache.values()) {
            if (testRun.equals(term.getTestRun())) {
                results.add(term);
            }
        }
        return results;
    }

    @Override
    public Term createTerm(String name) {
        log.debug("TermServer: createTerm()");
        final Term term = new Term();
        term.setUuid(UUID.randomUUID().toString());
        term.setName(name);
        cache.put(term.getUuid(), term);
        return term;
    }

    @Override
    public Term createTermForTesting(String name, TestRun testRun) {
        log.debug("TermServer: createTerm()");
        final Term term = createTerm(name);
        term.setTestRun(testRun);
        return term;
    }

    @Override
    public Term updateTerm(Term oldTerm, String name) {
        log.debug("TermServer: updateTerm()");
        if (!cache.containsKey(oldTerm.getUuid())) {
            throw new ObjectNotFoundException(oldTerm.getUuid());
        }

        final Term term = cache.get(oldTerm.getUuid());
        term.setUuid(UUID.randomUUID().toString());
        term.setTestRun(oldTerm.getTestRun());
        term.setName(name);
        return term;
    }

    @Override
    public void deleteTerm(String uuid, Integer version) {
        log.debug("TermServer: deleteTerm()");
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}
