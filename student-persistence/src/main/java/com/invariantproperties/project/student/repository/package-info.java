/**
 * Spring Data repositories.
 * 
 * Spring Data uses load-time class construction so we only need to
 * define our interface using standard conventions. The
 * {@link org.springframework.data.jpa.repository.JpaRepository}
 * interface defines all of the standard CRUD methods and the
 * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor}
 * interface allows us to use JPA Criteria Query searches.
 * 
 * @see <a href="http://invariantproperties.com/2013/12/19/project-student-persistence-with-spring-data/">Project Student: Persistence with Spring Data</a>
 * @see <a href="http://projects.spring.io/spring-data/">Spring Data</a>
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 *  
 * @author Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.repository;

