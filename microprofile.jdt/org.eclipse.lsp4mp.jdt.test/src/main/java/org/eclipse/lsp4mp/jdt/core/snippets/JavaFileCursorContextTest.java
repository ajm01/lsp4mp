/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.jdt.core.snippets;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ls.core.internal.ProjectUtils;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4jdt.commons.JavaCursorContextKind;
import org.eclipse.lsp4jdt.commons.JavaCompletionParams;
import org.eclipse.lsp4mp.jdt.core.BasePropertiesManagerTest;
import org.eclipse.lsp4mp.jdt.core.MPNewPropertiesManagerForJava;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the implementation of
 * <code>microprofile/java/javaCursorContext</code>.
 */
public class JavaFileCursorContextTest extends BasePropertiesManagerTest {

	private static final IProgressMonitor MONITOR = new NullProgressMonitor();

	@BeforeClass
	public static void setupProject() throws Exception {
		loadMavenProject(MicroProfileMavenProjectName.config_hover);
	}

	@After
	public void cleanUp() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/Empty.java"));
		javaFile.refreshLocal(IResource.DEPTH_ZERO, null);
		javaFile.setContents(new ByteArrayInputStream("".getBytes()), 0, MONITOR);
	}

	// context kind tests

	@Test
	public void testEmptyFileContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/Empty.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// |
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(0, 0));
		assertEquals(JavaCursorContextKind.IN_EMPTY_FILE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testJustSnippetFileContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/Empty.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();
		javaFile.refreshLocal(IResource.DEPTH_ZERO, null);
		javaFile.setContents(new ByteArrayInputStream("rest_class".getBytes()), 0, MONITOR);

		// rest_class|
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(0, "rest_class".length()));
		assertEquals(JavaCursorContextKind.IN_EMPTY_FILE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// |rest_class
		params = new JavaCompletionParams(javaFileUri, new Position(0, 0));
		assertEquals(JavaCursorContextKind.IN_EMPTY_FILE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// rest|_class
		params = new JavaCompletionParams(javaFileUri, new Position(0, 4));
		assertEquals(JavaCursorContextKind.IN_EMPTY_FILE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testBeforeFieldContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/GreetingResource.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// @ConfigProperty(name = "greeting.message")
		// |String message;
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(15, 4));
		assertEquals(JavaCursorContextKind.IN_FIELD_ANNOTATIONS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// |@ConfigProperty(name = "greeting.message")
		// String message;
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(14, 4));
		assertEquals(JavaCursorContextKind.BEFORE_FIELD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testBeforeMethodContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/GreetingResource.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// @GET
		// @Produces(MediaType.TEXT_PLAIN)
		// |public String hello() {
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(34, 4));
		assertEquals(JavaCursorContextKind.IN_METHOD_ANNOTATIONS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// |@GET
		// @Produces(MediaType.TEXT_PLAIN)
		// public String hello() {
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(32, 4));
		assertEquals(JavaCursorContextKind.BEFORE_METHOD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testInMethodContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/GreetingResource.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// @GET
		// @Produces(MediaType.TEXT_PLAIN)
		// public String hello() {
		// | return message + " " + name.orElse("world") + suffix;
		// }
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(35, 0));
		assertEquals(JavaCursorContextKind.NONE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// @GET
		// @Produces(MediaType.TEXT_PLAIN)
		// p|ublic String hello() {
		// return message + " " + name.orElse("world") + suffix;
		// }
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(34, 5));
		assertEquals(JavaCursorContextKind.NONE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testInClassContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/GreetingResource.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// public String hello() {
		// return message + " " + name.orElse("world") + suffix;
		// }
		// |}
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(37, 0));
		assertEquals(JavaCursorContextKind.IN_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testAfterClassContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/GreetingResource.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// public String hello() {
		// return message + " " + name.orElse("world") + suffix;
		// }
		// }
		// |
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(38, 0));
		assertEquals(JavaCursorContextKind.NONE,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testClassContextUsingInterface() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/MyInterface.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// public interface MyInterface {
		// |
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(3, 0));
		assertEquals(JavaCursorContextKind.BEFORE_FIELD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// public interface MyInterface {
		// ...
		// public void helloWorld();
		// |
		// }
		params = new JavaCompletionParams(javaFileUri, new Position(7, 0));
		assertEquals(JavaCursorContextKind.IN_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testClassContextUsingEnum() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/MyEnum.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// public enum MyEnum {
		// |
		// VALUE;
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(3, 0));
		assertEquals(JavaCursorContextKind.BEFORE_FIELD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// public enum MyEnum {
		// ...
		// |
		// public void helloWorld();
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(7, 0));
		assertEquals(JavaCursorContextKind.BEFORE_METHOD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// public enum MyEnum {
		// ...
		// public void helloWorld();
		// |
		// }
		params = new JavaCompletionParams(javaFileUri, new Position(9, 0));
		assertEquals(JavaCursorContextKind.IN_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testClassContextUsingAnnotation() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/MyAnnotation.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// public @interface MyAnnotation {
		// |
		// public static String MY_STRING = "asdf";
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(3, 0));
		assertEquals(JavaCursorContextKind.BEFORE_FIELD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// public @interface MyAnnotation {
		// ...
		// |
		// public String value() default MY_STRING;
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(5, 0));
		assertEquals(JavaCursorContextKind.BEFORE_METHOD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// public @interface MyAnnotation {
		// ...
		// public String value() default MY_STRING;
		// |
		// }
		params = new JavaCompletionParams(javaFileUri, new Position(7, 0));
		assertEquals(JavaCursorContextKind.IN_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	@Test
	public void testBeforeClassContext() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/MyNestedClass.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// ...
		// @Singleton
		// public class MyNestedClass {
		// |
		// @Singleton
		// static class MyNestedNestedClass {
		// ...
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(4, 0));
		assertEquals(JavaCursorContextKind.BEFORE_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// @Singleton
		// public class MyNestedClass {
		//
		// |@Singleton
		// static class MyNestedNestedClass {
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(5, 0));
		assertEquals(JavaCursorContextKind.BEFORE_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// @Singleton
		// public class MyNestedClass {
		//
		// @Singleton
		// | static class MyNestedNestedClass {
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(6, 0));
		assertEquals(JavaCursorContextKind.IN_CLASS_ANNOTATIONS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// |
		// @Singleton
		// public class MyNestedClass {
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(1, 0));
		assertEquals(JavaCursorContextKind.BEFORE_CLASS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		// ...
		// @Singleton
		// |public class MyNestedClass {
		// ...
		params = new JavaCompletionParams(javaFileUri, new Position(3, 0));
		assertEquals(JavaCursorContextKind.IN_CLASS_ANNOTATIONS,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

	// prefix tests

	@Test
	public void testAtBeginningOfFile() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/Empty.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		// |
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(0, 0));
		assertEquals("", MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getPrefix());
	}

	@Test
	public void testOneWord() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/Empty.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();
		javaFile.refreshLocal(IResource.DEPTH_ZERO, null);
		javaFile.setContents(new ByteArrayInputStream("rest_class".getBytes()), 0, MONITOR);

		// rest_class|
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri,
				new Position(0, "rest_class".length()));
		assertEquals("rest_class", MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getPrefix());

		// |rest_class
		params = new JavaCompletionParams(javaFileUri, new Position(0, 0));
		assertEquals("", MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getPrefix());

		// rest_|class
		params = new JavaCompletionParams(javaFileUri, new Position(0, 5));
		assertEquals("rest_", MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getPrefix());
	}

	@Test
	public void testTwoWords() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/Empty.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();
		javaFile.refreshLocal(IResource.DEPTH_ZERO, null);
		javaFile.setContents(new ByteArrayInputStream("asdf hjkl".getBytes()), 0, MONITOR);

		// asdf hjk|l
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(0, 8));
		assertEquals("hjk", MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getPrefix());

		// asdf |hjkl
		params = new JavaCompletionParams(javaFileUri, new Position(0, 5));
		assertEquals("", MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getPrefix());
	}

	@Test
	public void testLombok() throws Exception {
		IProject project = ProjectUtils.getProject(MicroProfileMavenProjectName.config_hover);;
		IFile javaFile = project.getFile(new Path("src/main/java/org/acme/config/WithLombok.java"));
		String javaFileUri = javaFile.getLocation().toFile().toURI().toString();

		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, new Position(6, 0));
		assertEquals(JavaCursorContextKind.BEFORE_FIELD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());

		params = new JavaCompletionParams(javaFileUri, new Position(8, 0));
		assertEquals(JavaCursorContextKind.BEFORE_METHOD,
				MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDT_UTILS, MONITOR).getKind());
	}

}
