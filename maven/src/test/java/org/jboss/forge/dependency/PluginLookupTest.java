/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.dependency;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.maven.dependency.Dependency;
import org.jboss.forge.maven.dependency.DependencyBuilder;
import org.jboss.forge.maven.dependency.DependencyQueryBuilder;
import org.jboss.forge.maven.dependency.DependencyRepository;
import org.jboss.forge.maven.dependency.DependencyResolver;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PluginLookupTest
{
   @Deployment
   public static ForgeArchive createTestArchive()
   {
      return ShrinkWrap.create(ForgeArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
               .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
   }

   @Inject
   private DependencyResolver resolver;

   @Test
   public void testResolveNonJarArtifact() throws Exception
   {

      Dependency dep = DependencyBuilder.create("org.jboss.forge:forge-example-plugin:2.0.0-SNAPSHOT")
               .setPackagingType("far");
      DependencyQueryBuilder query = DependencyQueryBuilder.create(dep).setRepositories(
               DependencyRepository.JBOSS_NEXUS);
      Set<Dependency> artifacts = resolver.resolveDependencies(query);
      for (Dependency dependencyResource : artifacts)
      {
         if ("far".equals(dependencyResource.getPackagingType()))
         {
            System.out.println(dependencyResource.getScopeType());
            System.out.println("PLUGIN: " + dependencyResource);
         }
      }
   }
}
