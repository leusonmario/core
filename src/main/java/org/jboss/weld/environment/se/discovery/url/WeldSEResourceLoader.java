/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.environment.se.discovery.url;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoadingException;
import org.jboss.weld.util.collections.EnumerationList;
import org.jboss.weld.util.reflection.Reflections;

/**
 * A simple resource loader.
 * 
 * Uses {@link WeldSEResourceLoader}'s classloader if the Thread Context 
 * Classloader isn't available
 * 
 * @author Pete Muir
 *
 */
public class WeldSEResourceLoader implements ResourceLoader
{
   
   public Class<?> classForName(String name)
   {
      
      try
      {
         Class<?> clazz = null;
         if (Thread.currentThread().getContextClassLoader() != null)
         {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(name);
         }
         else
         {
            clazz = Class.forName(name);
         }
         // if the class relies on optional dependencies that are not present
         // then a CNFE can be thrown later in the deployment process when the
         // Introspector is inspecting the class. We call getMethods, getFields
         // and getConstructors now over the whole type heirachey to force 
         // these errors to occur early
         Class<?> obj = clazz;
         while (obj != null && obj != Object.class)
         {
            obj.getDeclaredConstructors();
            obj.getDeclaredFields();
            obj.getDeclaredMethods();
            obj = obj.getSuperclass();
         }
         return clazz;
      }
      catch (ClassNotFoundException e)
      {
         throw new ResourceLoadingException(e);
      }
      catch (NoClassDefFoundError e)
      {
         throw new ResourceLoadingException(e);
      }
   }
   
   public URL getResource(String name)
   {
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         return Thread.currentThread().getContextClassLoader().getResource(name);
      }
      else
      {
         return getClass().getResource(name);
      }
   }
   
   public Collection<URL> getResources(String name)
   {
      try
      {
         if (Thread.currentThread().getContextClassLoader() != null)
         {
            return new EnumerationList<URL>(Thread.currentThread().getContextClassLoader().getResources(name));
         }
         else
         {
            return new EnumerationList<URL>(getClass().getClassLoader().getResources(name));
         }
      }
      catch (IOException e)
      {
         throw new ResourceLoadingException(e);
      }
   }
   
   public void cleanup() {}
   
}
