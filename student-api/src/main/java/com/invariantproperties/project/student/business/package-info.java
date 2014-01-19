/**
 * Public API for business layer.
 * 
 * The services fall into two broad categories.
 * 
 * <strong>Finder Services</strong> can be used to retrieve specific entities or
 * perform searches. None of these methods require read-write access to the underlying
 * persistence layer.
 * 
 * <strong>Manager Services</strong> can be used to create, modify or delete entities.
 * All methods require read-write access to the underlying persistence layer.
 *  
 * @author Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.business;