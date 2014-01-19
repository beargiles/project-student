/**
 * Specifications used by JPA Criteria Queries.
 * 
 * A {@link org.springframework.data.jpa.domain.Specification} is a
 * Spring Data interface with a single method, {@link org.springframework.data.jpa.domain.Specification#toPredicate},
 * that contains code that uses the metamodel classes defined elsewhere to create JPA
 * Criteria {@link javax.persistence.criteria.Predicate}s. This allows us to create
 * very advanced queries in a database-agnostic fashion.
 * 
 * Each of these classes contains one or more static methods to produce the
 * appropriate {@link org.springframework.data.jpa.domain.Specification} implementation.
 * 
 * @see <a href="http://invariantproperties.com/2013/12/19/project-student-persistence-with-spring-data/">Project Student: Persistence with Spring Data</a>
 * @see <a href="http://invariantproperties.com/2013/12/29/project-student-jpa-criteria-queries/">Project Student: JPA Criteria Queries</a>
 * @see <a href="http://projects.spring.io/spring-data/">Spring Data</a>
 * @see <a href="http://docs.oracle.com/javaee/6/tutorial/doc/gjitv.html">JEE Tutorial</a>
 * @see org.springframework.data.jpa.domain.Specification
 * @see javax.persistence.criteria.Predicate
 * @see com.invariantproperties.project.student.metamodel
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.specification;