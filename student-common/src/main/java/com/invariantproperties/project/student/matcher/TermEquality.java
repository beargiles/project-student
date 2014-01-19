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
package com.invariantproperties.project.student.matcher;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.invariantproperties.project.student.domain.Term;

/**
 * Hamcrest matcher for 'Term' class.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TermEquality extends TypeSafeMatcher<Term> {
    private final Term expected;

    private TermEquality(Term expected) {
        this.expected = expected;
    }

    @Override
    public boolean matchesSafely(Term actual) {
        if (actual == null) {
            return false;
        }

        EqualsBuilder eq = new EqualsBuilder();
        eq.append(expected.getId(), actual.getId());
        eq.append(expected.getUuid(), actual.getUuid());
        eq.append(expected.getName(), actual.getName());
        eq.append(expected.getCreationDate(), actual.getCreationDate());

        return eq.isEquals();
    }

    public void describeTo(Description description) {
        description.appendText("Term comparison");
    }

    @Factory
    public static <T> Matcher<Term> equalTo(Term term) {
        return new TermEquality(term);
    }
}
