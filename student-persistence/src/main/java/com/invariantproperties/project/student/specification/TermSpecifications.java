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
package com.invariantproperties.project.student.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.metamodel.Term_;
import com.invariantproperties.project.student.metamodel.TestRun_;

/**
 * JPA Criteria specifications for terms.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TermSpecifications {

    /**
     * Creates a specification used to find terms with the specified testUuid.
     * 
     * @param testRun
     * @return
     */
    public static Specification<Term> testRunIs(final TestRun testRun) {

        return new Specification<Term>() {
            @Override
            public Predicate toPredicate(Root<Term> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p = null;
                if (testRun == null || testRun.getUuid() == null) {
                    p = cb.isNull(root.<Term_> get("testRun"));
                } else {
                    p = cb.equal(root.<Term_> get("testRun").<TestRun_> get("uuid"), testRun.getUuid());
                }
                return p;
            }
        };
    }
}