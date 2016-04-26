package com.blade.kit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import blade.kit.StringKit;

/**
 * Unit tests for StringKit
 *
 * @author <a href="mailto:1993sj1993@gmail.com" target="_blank">gavinfish</a>
 */
public class StringKitTest {
	private static final String ALPHABET_LOWWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String ALPHABET_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Test
	public void testIsEmpty() {
		assertTrue(StringKit.isEmpty(null));
		assertTrue(StringKit.isEmpty(""));
		assertFalse(StringKit.isEmpty(" "));
		assertFalse(StringKit.isEmpty("foo"));
		assertFalse(StringKit.isEmpty(" foo "));
	}

	@Test
	public void testIsNotEmpty() {
		assertFalse(StringKit.isNotEmpty(null));
		assertFalse(StringKit.isNotEmpty(""));
		assertTrue(StringKit.isNotEmpty(" "));
		assertTrue(StringKit.isNotEmpty("foo"));
		assertTrue(StringKit.isNotEmpty(" foo "));
	}

	@Test
	public void testIsAnyEmpty() {
		assertTrue(StringKit.isAnyEmpty((String) null));
		assertTrue(StringKit.isAnyEmpty((String[]) null));
		assertTrue(StringKit.isAnyEmpty("", "foo"));
		assertTrue(StringKit.isAnyEmpty("foo", "bar", ""));
		assertTrue(StringKit.isAnyEmpty(null, "bar"));
		assertTrue(StringKit.isAnyEmpty("bar", null, " foo "));
		assertFalse(StringKit.isAnyEmpty("foo", "bar"));
		assertFalse(StringKit.isAnyEmpty(" ", "bar"));
	}

	@Test
	public void testIsBlank() {
		assertTrue(StringKit.isBlank(null));
		assertTrue(StringKit.isBlank(""));
		assertTrue(StringKit.isBlank(" "));
		assertFalse(StringKit.isBlank("foo"));
		assertFalse(StringKit.isBlank(" foo "));
	}

	@Test
	public void testIsNotBlank() {
		assertFalse(StringKit.isNotBlank(null));
		assertFalse(StringKit.isNotBlank(""));
		assertFalse(StringKit.isNotBlank(" "));
		assertTrue(StringKit.isNotBlank("foo"));
		assertTrue(StringKit.isNotBlank(" foo "));
	}

	@Test
	public void testIsAllBlank() {
		assertTrue(StringKit.isAllBlank((String) null));
		assertTrue(StringKit.isAllBlank((String[]) null));
		assertFalse(StringKit.isAllBlank("", "foo"));
		assertFalse(StringKit.isAllBlank("foo", "bar", ""));
		assertFalse(StringKit.isAllBlank(null, "bar"));
		assertFalse(StringKit.isAllBlank("bar", null, " foo "));
		assertFalse(StringKit.isAllBlank("foo", "bar"));
		assertFalse(StringKit.isAllBlank(" ", "bar"));
		assertTrue(StringKit.isAllBlank(null, null, ""));
	}

	@Test
	public void testIsAnyBlank() {
		assertTrue(StringKit.isAnyBlank((String) null));
		assertTrue(StringKit.isAnyBlank((String[]) null));
		assertTrue(StringKit.isAnyBlank("", "foo"));
		assertTrue(StringKit.isAnyBlank("foo", "bar", ""));
		assertTrue(StringKit.isAnyBlank(null, "bar"));
		assertTrue(StringKit.isAnyBlank("bar", null, " foo "));
		assertFalse(StringKit.isAnyBlank("foo", "bar"));
		assertTrue(StringKit.isAnyBlank(" ", "bar"));
		assertTrue(StringKit.isAnyBlank(null, null, ""));
	}

	@Test
	public void testDefaultIfNull_String() {
		assertEquals("", StringKit.defaultIfNull(null));
		assertEquals("", StringKit.defaultIfNull(""));
		assertEquals("foo", StringKit.defaultIfNull("foo"));
	}

	@Test
	public void testDefaultIfNull_StringString() {
		assertEquals("NULL", StringKit.defaultIfNull(null, "NULL"));
		assertEquals("", StringKit.defaultIfNull("", "NULL"));
		assertEquals("foo", StringKit.defaultIfNull("foo", "NULL"));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testTrim_str() {
		assertNull(StringKit.trim(null));
		assertEquals("", StringKit.trim(""));
		assertEquals("", StringKit.trim("  \n\t\r  "));
		assertEquals("foo", StringKit.trim("foo"));
		assertEquals("foo", StringKit.trim("foo  "));
		assertEquals("foo", StringKit.trim("  foo"));
		assertEquals("foo", StringKit.trim("  foo  "));
		assertEquals("\b", StringKit.trim("\b"));
	}

	@Test
	public void testTrim_StrStr() {
		assertNull(StringKit.trim(null, null));
		assertEquals("", StringKit.trim("", null));
		assertEquals("", StringKit.trim("  \n\t\r  ", null));
		assertEquals("foo", StringKit.trim("foo", null));
		assertEquals("foo", StringKit.trim("foo  ", null));
		assertEquals("foo", StringKit.trim("  foo", null));
		assertEquals("foo", StringKit.trim("  foo  ", null));
		assertEquals("\b", StringKit.trim("\b", null));

		assertNull(StringKit.trim(null, "o"));
		assertEquals("", StringKit.trim("foo", "fo"));
		assertEquals("foo", StringKit.trim("foo", ""));
		assertEquals("f  ", StringKit.trim("oof  ", "o"));
		assertEquals("  f", StringKit.trim("  foo", "o"));
		assertEquals("f", StringKit.trim("oofoo", "o"));
		assertEquals(" bar ", StringKit.trim("foo bar foo", "of"));
		assertEquals("  abc", StringKit.trim("  abcyx", "xyz"));
	}

	@Test
	public void testTrimStart_Str() {
		assertNull(StringKit.trimStart(null));
		assertEquals("", StringKit.trimStart(""));
		assertEquals("", StringKit.trimStart("  \n\t\r  "));
		assertEquals("foo", StringKit.trimStart("foo"));
		assertEquals("foo  ", StringKit.trimStart("foo  "));
		assertEquals("foo", StringKit.trimStart("  foo"));
		assertEquals("foo  ", StringKit.trimStart("  foo  "));
		assertEquals("\b", StringKit.trimStart("\b"));
	}

	@Test
	public void testTrimStart_StrStr() {
		assertNull(StringKit.trimStart(null, null));
		assertEquals("", StringKit.trimStart("", null));
		assertEquals("", StringKit.trimStart("  \n\t\r  ", null));
		assertEquals("foo", StringKit.trimStart("foo", null));
		assertEquals("foo  ", StringKit.trimStart("foo  ", null));
		assertEquals("foo", StringKit.trimStart("  foo", null));
		assertEquals("foo  ", StringKit.trimStart("  foo  ", null));
		assertEquals("\b", StringKit.trimStart("\b", null));

		assertNull(StringKit.trimStart(null, "o"));
		assertEquals("", StringKit.trimStart("foo", "fo"));
		assertEquals("foo", StringKit.trimStart("foo", ""));
		assertEquals("f  ", StringKit.trimStart("oof  ", "o"));
		assertEquals("  foo", StringKit.trimStart("  foo", "o"));
		assertEquals("foo", StringKit.trimStart("oofoo", "o"));
		assertEquals(" bar foo", StringKit.trimStart("foo bar foo", "of"));
		assertEquals("  abcyx", StringKit.trimStart("  abcyx", "xyz"));
	}

	@Test
	public void testTrimEnd_Str() {
		assertNull(StringKit.trimEnd(null));
		assertEquals("", StringKit.trimEnd(""));
		assertEquals("", StringKit.trimEnd("  \n\t\r  "));
		assertEquals("foo", StringKit.trimEnd("foo"));
		assertEquals("foo", StringKit.trimEnd("foo  "));
		assertEquals("  foo", StringKit.trimEnd("  foo"));
		assertEquals("  foo", StringKit.trimEnd("  foo  "));
		assertEquals("\b", StringKit.trimEnd("\b"));
	}

	@Test
	public void testTrimEnd_StrStr() {
		assertNull(StringKit.trimEnd(null, null));
		assertEquals("", StringKit.trimEnd("", null));
		assertEquals("", StringKit.trimEnd("  \n\t\r  ", null));
		assertEquals("foo", StringKit.trimEnd("foo", null));
		assertEquals("foo", StringKit.trimEnd("foo  ", null));
		assertEquals("  foo", StringKit.trimEnd("  foo", null));
		assertEquals("  foo", StringKit.trimEnd("  foo  ", null));
		assertEquals("\b", StringKit.trimEnd("\b", null));

		assertNull(StringKit.trimEnd(null, "o"));
		assertEquals("", StringKit.trimEnd("foo", "fo"));
		assertEquals("foo", StringKit.trimEnd("foo", ""));
		assertEquals("oof  ", StringKit.trimEnd("oof  ", "o"));
		assertEquals("  f", StringKit.trimEnd("  foo", "o"));
		assertEquals("oof", StringKit.trimEnd("oofoo", "o"));
		assertEquals("foo bar ", StringKit.trimEnd("foo bar foo", "of"));
		assertEquals("  abc", StringKit.trimEnd("  abcyx", "xyz"));
	}

	@Test
	public void testTrimToNull_Str() {
		assertNull(StringKit.trimToNull(null));
		assertNull(StringKit.trimToNull(""));
		assertNull(StringKit.trimToNull("  \n\t\r  "));
		assertEquals("foo", StringKit.trimToNull("foo"));
		assertEquals("foo", StringKit.trimToNull("foo  "));
		assertEquals("foo", StringKit.trimToNull("  foo"));
		assertEquals("foo", StringKit.trimToNull("  foo  "));
		assertEquals("\b", StringKit.trimToNull("\b"));
	}

	@Test
	public void testTrimToNull_StrStr() {
		assertNull(StringKit.trimToNull(null, null));
		assertNull(StringKit.trimToNull("", null));
		assertNull(StringKit.trimToNull("  \n\t\r  ", null));
		assertEquals("foo", StringKit.trimToNull("foo", null));
		assertEquals("foo", StringKit.trimToNull("foo  ", null));
		assertEquals("foo", StringKit.trimToNull("  foo", null));
		assertEquals("foo", StringKit.trimToNull("  foo  ", null));
		assertEquals("\b", StringKit.trimToNull("\b", null));

		assertNull(StringKit.trimToNull(null, "o"));
		assertNull(StringKit.trimToNull("foo", "fo"));
		assertEquals("foo", StringKit.trimToNull("foo", ""));
		assertEquals("f  ", StringKit.trimToNull("oof  ", "o"));
		assertEquals("  f", StringKit.trimToNull("  foo", "o"));
		assertEquals("f", StringKit.trimToNull("oofoo", "o"));
		assertEquals(" bar ", StringKit.trimToNull("foo bar foo", "of"));
		assertEquals("  abc", StringKit.trimToNull("  abcyx", "xyz"));
	}

	@Test
	public void testTrimToEmpty_Str() {
		assertEquals("", StringKit.trimToEmpty(null));
		assertEquals("", StringKit.trimToEmpty(""));
		assertEquals("", StringKit.trimToEmpty("  \n\t\r  "));
		assertEquals("foo", StringKit.trimToEmpty("foo"));
		assertEquals("foo", StringKit.trimToEmpty("foo  "));
		assertEquals("foo", StringKit.trimToEmpty("  foo"));
		assertEquals("foo", StringKit.trimToEmpty("  foo  "));
		assertEquals("\b", StringKit.trimToEmpty("\b"));
	}

	@Test
	public void testTrimToEmpty_StrStr() {
		assertEquals("", StringKit.trimToEmpty(null, null));
		assertEquals("", StringKit.trimToEmpty("", null));
		assertEquals("", StringKit.trimToEmpty("  \n\t\r  ", null));
		assertEquals("foo", StringKit.trimToEmpty("foo", null));
		assertEquals("foo", StringKit.trimToEmpty("foo  ", null));
		assertEquals("foo", StringKit.trimToEmpty("  foo", null));
		assertEquals("foo", StringKit.trimToEmpty("  foo  ", null));
		assertEquals("\b", StringKit.trimToEmpty("\b", null));

		assertEquals("", StringKit.trimToEmpty(null, "o"));
		assertEquals("", StringKit.trimToEmpty("foo", "fo"));
		assertEquals("foo", StringKit.trimToEmpty("foo", ""));
		assertEquals("f  ", StringKit.trimToEmpty("oof  ", "o"));
		assertEquals("  f", StringKit.trimToEmpty("  foo", "o"));
		assertEquals("f", StringKit.trimToEmpty("oofoo", "o"));
		assertEquals(" bar ", StringKit.trimToEmpty("foo bar foo", "of"));
		assertEquals("  abc", StringKit.trimToEmpty("  abcyx", "xyz"));
	}

	// ---------------------------------------------------------------------

	@Test
	public void testEquals() {
		assertTrue(StringKit.equals(null, null));
		assertTrue(StringKit.equals("", ""));
		assertTrue(StringKit.equals("foo", "foo"));
		assertFalse(StringKit.equals("foo", "foO"));
		assertFalse(StringKit.equals("foo", "bar"));
		assertFalse(StringKit.equals("foo", null));
		assertFalse(StringKit.equals(null, "bar"));
		assertFalse(StringKit.equals(" foo ", "foo"));
		assertFalse(StringKit.equals("foo", " foo "));
		assertFalse(StringKit.equals("foo", "foobar"));
		assertFalse(StringKit.equals("foobar", "foo"));
	}

	@Test
	public void testEqualsIgnoreCase_String() {
		assertTrue(StringKit.equalsIgnoreCase((String) null, (String) null));
		assertTrue(StringKit.equalsIgnoreCase("", ""));
		assertTrue(StringKit.equalsIgnoreCase("foo", "foo"));
		assertTrue(StringKit.equalsIgnoreCase("foo", "foO"));
		assertFalse(StringKit.equalsIgnoreCase("foo", "bar"));
		assertFalse(StringKit.equalsIgnoreCase("foo", null));
		assertFalse(StringKit.equalsIgnoreCase(null, "bar"));
		assertFalse(StringKit.equalsIgnoreCase(" foo ", "foo"));
		assertFalse(StringKit.equalsIgnoreCase("foo", " foo "));
	}

	@Test
	public void testEqualsOne() {
		assertEquals(-1, StringKit.equalsOne("foo", (String[]) null));
		assertEquals(-1, StringKit.equalsOne("foo", new String[] {}));
		assertEquals(0, StringKit.equalsOne("foo", new String[] { "foo" }));
		assertEquals(1, StringKit.equalsOne("foo", new String[] { "bar", "foo" }));
		assertEquals(-1, StringKit.equalsOne("foo", new String[] { null, "bar" }));
		assertEquals(3, StringKit.equalsOne("foo", new String[] { "bar", "", null, "foo", "bar" }));
		assertEquals(-1, StringKit.equalsOne("foo", new String[] { "bar", "FOO" }));
		assertEquals(-1, StringKit.equalsOne("foo", new String[] { "bar" }));
		assertEquals(-1, StringKit.equalsOne("foo", new String[] { "foobar" }));
		assertEquals(-1, StringKit.equalsOne("foobar", new String[] { "foo" }));
		assertEquals(0, StringKit.equalsOne("foo", new String[] { "foo", "bar", "foo" }));
		assertEquals(1, StringKit.equalsOne("foo", new String[] { "bar", "foo", "foo" }));
		assertEquals(2, StringKit.equalsOne("foo", new String[] { "FOO", "bar", "foo" }));
		assertEquals(2, StringKit.equalsOne("foo", new String[] { "bar", "FOO", "foo" }));

		assertEquals(-1, StringKit.equalsOne(null, new String[] { "foo", "bar" }));
		assertEquals(-1, StringKit.equalsOne(null, (String[]) null));
		assertEquals(0, StringKit.equalsOne(null, new String[] { null, null, "foo" }));
		assertEquals(1, StringKit.equalsOne(null, new String[] { "foo", null, null, "bar" }));
	}

	@Test
	public void testEuqalsOneIgnoreCase() {
		assertEquals(-1, StringKit.equalsOneIgnoreCase("foo", (String[]) null));
		assertEquals(-1, StringKit.equalsOneIgnoreCase("foo", new String[] {}));
		assertEquals(0, StringKit.equalsOneIgnoreCase("foo", new String[] { "foo" }));
		assertEquals(1, StringKit.equalsOneIgnoreCase("foo", new String[] { "bar", "foo" }));
		assertEquals(-1, StringKit.equalsOneIgnoreCase("foo", new String[] { null, "bar" }));
		assertEquals(3, StringKit.equalsOneIgnoreCase("foo", new String[] { "bar", "", null, "foo", "bar" }));
		assertEquals(1, StringKit.equalsOneIgnoreCase("foo", new String[] { "bar", "FOO" }));
		assertEquals(-1, StringKit.equalsOneIgnoreCase("foo", new String[] { "bar" }));
		assertEquals(-1, StringKit.equalsOneIgnoreCase("foo", new String[] { "foobar" }));
		assertEquals(-1, StringKit.equalsOneIgnoreCase("foobar", new String[] { "foo" }));
		assertEquals(0, StringKit.equalsOneIgnoreCase("foo", new String[] { "foo", "bar", "foo" }));
		assertEquals(1, StringKit.equalsOneIgnoreCase("foo", new String[] { "bar", "foo", "foo" }));
		assertEquals(0, StringKit.equalsOneIgnoreCase("foo", new String[] { "FOO", "bar", "foo" }));
		assertEquals(1, StringKit.equalsOneIgnoreCase("foo", new String[] { "bar", "FOO", "foo" }));

		assertEquals(-1, StringKit.equalsOneIgnoreCase(null, new String[] { "foo", "bar" }));
		assertEquals(-1, StringKit.equalsOneIgnoreCase(null, (String[]) null));
		assertEquals(0, StringKit.equalsOneIgnoreCase(null, new String[] { null, null, "foo" }));
		assertEquals(1, StringKit.equalsOneIgnoreCase(null, new String[] { "foo", null, null, "bar" }));
	}

	@Test
	public void testEqualsIgnoreCase_StringArray() {
		assertTrue(StringKit.equalsIgnoreCase((String[]) null, (String[]) null));
		assertFalse(StringKit.equalsIgnoreCase((String[]) null, new String[] { "foo" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, (String[]) null));
		assertTrue(StringKit.equalsIgnoreCase(new String[] { "" }, new String[] { "" }));
		assertTrue(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { "foo" }));
		assertTrue(StringKit.equalsIgnoreCase(new String[] { "foo", "bar" }, new String[] { "foo", "bar" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo", "bar" }, new String[] { "bar", "foo" }));
		assertTrue(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { "foO" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { "bar" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { null }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { null }, new String[] { "bar" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { " foo " }, new String[] { "foo" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { " foo " }));

		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { "foo", "bar" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo", "bar" }, new String[] { "foo" }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { "foo", null }));
		assertFalse(StringKit.equalsIgnoreCase(new String[] { "foo" }, new String[] { null, "foo" }));
	}

	// ---------------------------------------------------------------------

	@Test
	public void testIsAlpha() {
		assertTrue(StringKit.isAlpha("a"));
		assertTrue(StringKit.isAlpha("A"));
		assertTrue(StringKit.isAlpha("foobarsunshineFOOBARSUNSHINE"));
		assertTrue(StringKit.isAlpha(""));
		assertFalse(StringKit.isAlpha(" "));
		assertFalse(StringKit.isAlpha(null));
		assertFalse(StringKit.isAlpha("1"));
		assertFalse(StringKit.isAlpha("*"));
		assertFalse(StringKit.isAlpha("foo2bar4sunshine"));
		assertFalse(StringKit.isAlpha("_str"));
		assertFalse(StringKit.isAlpha("foo bar"));
	}

	@Test
	public void testIsAlphaSpace() {
		assertTrue(StringKit.isAlphaSpace("a"));
		assertTrue(StringKit.isAlphaSpace("A"));
		assertTrue(StringKit.isAlphaSpace("foobarsunshineFOOBARSUNSHINE"));
		assertTrue(StringKit.isAlphaSpace(""));
		assertTrue(StringKit.isAlphaSpace(" "));
		assertFalse(StringKit.isAlphaSpace(null));
		assertFalse(StringKit.isAlphaSpace("1"));
		assertFalse(StringKit.isAlphaSpace("*"));
		assertFalse(StringKit.isAlphaSpace("foo2bar4sunshine"));
		assertFalse(StringKit.isAlphaSpace("_str"));
		assertTrue(StringKit.isAlphaSpace("foo bar"));
	}

	@Test
	public void testIsAlphanumeric() {
		assertTrue(StringKit.isAlphanumeric("a"));
		assertTrue(StringKit.isAlphanumeric("A"));
		assertTrue(StringKit.isAlphanumeric("foobarsunshineFOOBARSUNSHINE"));
		assertTrue(StringKit.isAlphanumeric(""));
		assertTrue(StringKit.isAlphanumeric("1"));
		assertTrue(StringKit.isAlphanumeric("foo2bar4sunshine"));
		assertFalse(StringKit.isAlphanumeric(" "));
		assertFalse(StringKit.isAlphanumeric(null));
		assertFalse(StringKit.isAlphanumeric("*"));
		assertFalse(StringKit.isAlphanumeric("_str"));
		assertFalse(StringKit.isAlphanumeric("foo bar"));
	}

	@Test
	public void testIsAlphanumbericSpace() {
		assertTrue(StringKit.isAlphanumericSpace("a"));
		assertTrue(StringKit.isAlphanumericSpace("A"));
		assertTrue(StringKit.isAlphanumericSpace("foobarsunshineFOOBARSUNSHINE"));
		assertTrue(StringKit.isAlphanumericSpace(""));
		assertTrue(StringKit.isAlphanumericSpace("1"));
		assertTrue(StringKit.isAlphanumericSpace("foo2bar4sunshine"));
		assertTrue(StringKit.isAlphanumericSpace(" "));
		assertFalse(StringKit.isAlphanumericSpace(null));
		assertFalse(StringKit.isAlphanumericSpace("*"));
		assertFalse(StringKit.isAlphanumericSpace("_str"));
		assertTrue(StringKit.isAlphanumericSpace("foo bar"));
	}

	@Test
	public void testIsNumeric() {
		assertFalse(StringKit.isNumeric("a"));
		assertFalse(StringKit.isNumeric("A"));
		assertFalse(StringKit.isNumeric("foobarsunshineFOOBARSUNSHINE"));
		assertTrue(StringKit.isNumeric(""));
		assertTrue(StringKit.isNumeric("1"));
		assertFalse(StringKit.isNumeric("foo2bar4sunshine"));
		assertFalse(StringKit.isNumeric(" "));
		assertFalse(StringKit.isNumeric(null));
		assertFalse(StringKit.isNumeric("*"));
		assertFalse(StringKit.isNumeric("_str"));
		assertFalse(StringKit.isNumeric("foo bar"));
		assertTrue(StringKit.isNumeric("10000"));
		assertFalse(StringKit.isNumeric("100 00"));
		assertFalse(StringKit.isNumeric("3.14"));
		assertFalse(StringKit.isNumeric("+100"));
		assertFalse(StringKit.isNumeric("-100"));
	}

	@Test
	public void testIsNumericSpace() {
		assertFalse(StringKit.isNumericSpace("a"));
		assertFalse(StringKit.isNumericSpace("A"));
		assertFalse(StringKit.isNumericSpace("foobarsunshineFOOBARSUNSHINE"));
		assertTrue(StringKit.isNumericSpace(""));
		assertTrue(StringKit.isNumericSpace("1"));
		assertFalse(StringKit.isNumericSpace("foo2bar4sunshine"));
		assertTrue(StringKit.isNumericSpace(" "));
		assertFalse(StringKit.isNumericSpace(null));
		assertFalse(StringKit.isNumericSpace("*"));
		assertFalse(StringKit.isNumericSpace("_str"));
		assertFalse(StringKit.isNumericSpace("foo bar"));
		assertTrue(StringKit.isNumericSpace("10000"));
		assertTrue(StringKit.isNumericSpace("100 00"));
		assertFalse(StringKit.isNumericSpace("3.14"));
		assertFalse(StringKit.isNumericSpace("+100"));
		assertFalse(StringKit.isNumericSpace("-100"));
	}

	@Test
	public void testIsWhitespace() {
		assertFalse(StringKit.isWhitespace(null));
		assertTrue(StringKit.isWhitespace(""));
		assertTrue(StringKit.isWhitespace(" "));
		assertFalse(StringKit.isWhitespace(" foo "));
		assertFalse(StringKit.isWhitespace("foo "));
		assertFalse(StringKit.isWhitespace(" foo"));
		assertFalse(StringKit.isWhitespace(" foo "));
		assertTrue(StringKit.isWhitespace("\t \n"));
		assertFalse(StringKit.isWhitespace("\t foo \n"));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testToUpperCase() {
		assertNull(StringKit.toUpperCase(null));
		assertEquals("", StringKit.toUpperCase(""));
		assertEquals("FOO", StringKit.toUpperCase("foo"));
		assertEquals("FOO", StringKit.toUpperCase("FOO"));
		assertEquals(" FOO BAR ", StringKit.toUpperCase(" fOo BaR "));
	}

	@Test
	public void testToLowerCase() {
		assertNull(StringKit.toLowerCase(null));
		assertEquals("", StringKit.toLowerCase(""));
		assertEquals("foo", StringKit.toLowerCase("foo"));
		assertEquals("foo", StringKit.toLowerCase("FOO"));
		assertEquals(" foo bar ", StringKit.toLowerCase(" fOo BaR "));
	}

	@Test
	public void testCapitalize() {
		assertNull(StringKit.capitalize(null));
		assertEquals("", StringKit.capitalize(""));
		assertEquals("Foo", StringKit.capitalize("foo"));
		assertEquals("FOo", StringKit.capitalize("fOo"));
		assertEquals("FOO", StringKit.capitalize("FOO"));
		assertEquals("'foo'", StringKit.capitalize("'foo'"));
	}

	@Test
	public void testUncapitalize() {
		assertNull(StringKit.uncapitalize(null));
		assertEquals("", StringKit.uncapitalize(""));
		assertEquals("foo", StringKit.uncapitalize("foo"));
		assertEquals("fOo", StringKit.uncapitalize("FOo"));
		assertEquals("fOO", StringKit.uncapitalize("FOO"));
		assertEquals("'foo'", StringKit.uncapitalize("'foo'"));
	}

	@Test
	public void testReCapitalize() {
		assertEquals("foo", StringKit.uncapitalize(StringKit.capitalize("foo")));
		assertEquals("Foo", StringKit.capitalize(StringKit.uncapitalize("Foo")));
		assertEquals("foo bar", StringKit.uncapitalize(StringKit.capitalize("foo bar")));
		assertEquals("Foo Bar", StringKit.capitalize(StringKit.uncapitalize("Foo Bar")));
	}

	@Test
	public void testDecapitalize() {
		assertNull(StringKit.decapitalize(null));
		assertEquals("", StringKit.decapitalize(""));
		assertEquals("a", StringKit.decapitalize("A"));
		assertEquals("foo", StringKit.decapitalize("Foo"));
		assertEquals("FOo", StringKit.decapitalize("FOo"));
		assertEquals("FOO", StringKit.decapitalize("FOO"));
		assertEquals("'foo'", StringKit.decapitalize("'foo'"));
		assertEquals("\u01C9TitleCase", StringKit.decapitalize("\u01C8TitleCase"));
		assertEquals("\u01C9titlecase", StringKit.decapitalize("\u01C8titlecase"));
	}

	@Test
	public void testSwapCase() {
		assertNull(StringKit.swapCase(null));
		assertEquals("", StringKit.swapCase(""));
		assertEquals(" ", StringKit.swapCase(" "));
		assertEquals("a", StringKit.swapCase("A"));
		assertEquals("A", StringKit.swapCase("a"));
		assertEquals("foo bar 100", StringKit.swapCase("FOO BAR 100"));
		assertEquals("FOO BAR 100", StringKit.swapCase("foo bar 100"));
		assertEquals("foo bAR 100", StringKit.swapCase("FOO Bar 100"));
		assertEquals("Foo bAr 100", StringKit.swapCase("fOO BaR 100"));
		assertEquals("tITLEcASE: \u01C9", StringKit.swapCase("TitleCase: \u01C8"));
	}

	@Test
	public void testFromCamelCase() {
		assertNull(StringKit.fromCamelCase(null, '-'));
		assertEquals("", StringKit.fromCamelCase("", '-'));
		assertEquals("camel-case", StringKit.fromCamelCase("camelCase", '-'));
		assertEquals("camel-case", StringKit.fromCamelCase("CamelCase", '-'));
		assertEquals("camel-case-word", StringKit.fromCamelCase("camelCASEWord", '-'));
		assertEquals("asf_rules", StringKit.fromCamelCase("asfRULES", '_'));
		assertEquals("asf_rules", StringKit.fromCamelCase("ASFRules", '_'));
	}

	// TODO: add more test cases for to** methods

	@Test
	public void testToShort() {
		assertEquals(100, StringKit.toShort(null, (short) 100));
		assertEquals(100, StringKit.toShort("", (short) 100));
		assertEquals(100, StringKit.toShort("foo", (short) 100));
		assertEquals(1314, StringKit.toShort("1314", (short) 100));
		assertEquals(100, StringKit.toShort("foo121", (short) 100.1));
		assertEquals(100, StringKit.toShort("foo121", (short) 100.9));
		assertEquals(123, StringKit.toShort("+123", (short) 100));
		assertEquals(-123, StringKit.toShort("-123", (short) 100));
		assertEquals(Short.MAX_VALUE, StringKit.toShort("2000000", Short.MAX_VALUE));
		assertEquals(Short.MIN_VALUE, StringKit.toShort("-2000000", Short.MIN_VALUE));
	}

	@Test
	public void testToInt() {
		assertEquals(100, StringKit.toInt(null, 100));
		assertEquals(100, StringKit.toInt("", 100));
		assertEquals(100, StringKit.toInt("foo", 100));
		assertEquals(1314, StringKit.toInt("1314", 100));
		assertEquals(100, StringKit.toInt("foo121", (int) 100.1));
		assertEquals(100, StringKit.toInt("foo121", (int) 100.9));
		assertEquals(123, StringKit.toInt("+123", 100));
		assertEquals(-123, StringKit.toInt("-123", 100));
		assertEquals(Integer.MAX_VALUE, StringKit.toInt("20000000000", Integer.MAX_VALUE));
		assertEquals(Integer.MIN_VALUE, StringKit.toInt("-20000000000", Integer.MIN_VALUE));
	}

	@Test
	public void testToLong() {
		assertEquals(100, StringKit.toLong(null, 100));
		assertEquals(100, StringKit.toLong("", 100));
		assertEquals(100, StringKit.toLong("foo", 100));
		assertEquals(1314, StringKit.toLong("1314", 100));
		assertEquals(100, StringKit.toLong("foo121", (int) 100.1));
		assertEquals(100, StringKit.toLong("foo121", (int) 100.9));
		assertEquals(123, StringKit.toLong("+123", 100));
		assertEquals(-123, StringKit.toLong("-123", 100));
		assertEquals(Long.MAX_VALUE, StringKit.toLong("200000000000000000000", Long.MAX_VALUE));
		assertEquals(Long.MIN_VALUE, StringKit.toLong("-200000000000000000000", Long.MIN_VALUE));
		assertEquals(100, StringKit.toLong("1314L", 100));
	}

	@Test
	public void testToFloat() {
		assertEquals((float) 100.1, StringKit.toFloat(null, (float) 100.1), 0);
		assertEquals((float) 100.1, StringKit.toFloat("", (float) 100.1), 0);
		assertEquals((float) 100.1, StringKit.toFloat("foo", (float) 100.1), 0);
		assertEquals((float) 1314, StringKit.toFloat("1314", (float) 100.1), 0);
		assertEquals((float) 100.1, StringKit.toFloat("foo121", (float) 100.1), 0);
		assertEquals((float) 100.9, StringKit.toFloat("foo121", (float) 100.9), 0);
		assertEquals((float) 123.4, StringKit.toFloat("+123.4", (float) 100), 0);
		assertEquals((float) -123.4, StringKit.toFloat("-123.4", (float) 100), 0);
	}

	@Test
	public void testToDouble() {
		assertEquals(100.1, StringKit.toDouble(null, (double) 100.1), 0);
		assertEquals(100.1, StringKit.toDouble("", (double) 100.1), 0);
		assertEquals(100.1, StringKit.toDouble("foo", (double) 100.1), 0);
		assertEquals(1314, StringKit.toDouble("1314", (double) 100.1), 0);
		assertEquals(100.1, StringKit.toDouble("foo121", (double) 100.1), 0);
		assertEquals(100.9, StringKit.toDouble("foo121", (double) 100.9), 0);
		assertEquals(123.4, StringKit.toDouble("+123.4", (double) 100), 0);
		assertEquals(-123.4, StringKit.toDouble("-123.4", (double) 100), 0);
	}

	@Test
	public void testToBoolean() {
		assertTrue(StringKit.toBoolean(null, true));
		assertTrue(StringKit.toBoolean("", true));
		assertFalse(StringKit.toBoolean(null, false));
		assertFalse(StringKit.toBoolean("", false));
		assertFalse(StringKit.toBoolean("foo", true));
		assertTrue(StringKit.toBoolean("true", false));
		assertTrue(StringKit.toBoolean("TRUE", false));
		assertTrue(StringKit.toBoolean("TrUe", false));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testGetDelFormat() {
		assertEquals("10.0", StringKit.getDelFormat(10.000f));
		assertEquals("10.0", StringKit.getDelFormat(10.04f));
		assertEquals("10.0", StringKit.getDelFormat(10f));
		assertEquals("9.9", StringKit.getDelFormat(9.95f));
		assertEquals("10.1", StringKit.getDelFormat(10.05f));

		assertEquals("10.0", StringKit.getDelFormat(10.000));
		assertEquals("10.0", StringKit.getDelFormat(10.04));
		assertEquals("10.0", StringKit.getDelFormat(10));
		assertEquals("9.9", StringKit.getDelFormat(9.95));
		assertEquals("10.1", StringKit.getDelFormat(10.05));
	}

	@Test
	public void testGetDelFormat2() {
		assertEquals("10.00", StringKit.getDelFormat2(10.000f));
		assertEquals("10.00", StringKit.getDelFormat2(10.004f));
		assertEquals("10.00", StringKit.getDelFormat2(10f));
		assertEquals("9.99", StringKit.getDelFormat2(9.995f));
		assertEquals("10.01", StringKit.getDelFormat2(10.005f));

		assertEquals("10.00", StringKit.getDelFormat2(10.000));
		assertEquals("10.00", StringKit.getDelFormat2(10.004));
		assertEquals("10.00", StringKit.getDelFormat2(10d));
		assertEquals("9.99", StringKit.getDelFormat2(9.995));
		assertEquals("10.01", StringKit.getDelFormat2(10.005));
	}

	@Test
	public void testGetDelFormat3() {
		assertEquals("10.000", StringKit.getDelFormat3(10.0000f));
		assertEquals("10.000", StringKit.getDelFormat3(10.0004f));
		assertEquals("10.000", StringKit.getDelFormat3(10f));
		// This two cases are wired since float and double are not accurate
		assertEquals("10.000", StringKit.getDelFormat3(9.9995f));
		assertEquals("10.000", StringKit.getDelFormat3(10.0005f));

		assertEquals("10.000", StringKit.getDelFormat3(10.0000));
		assertEquals("10.000", StringKit.getDelFormat3(10.0004));
		assertEquals("10.000", StringKit.getDelFormat3(10d));
		assertEquals("9.999", StringKit.getDelFormat3(9.9995));
		assertEquals("10.001", StringKit.getDelFormat3(10.0005));
	}

	@Test
	public void testGetStringsubstr() {
		assertEquals("", StringKit.getStringsubstr(null, 1));
		assertEquals("", StringKit.getStringsubstr("", 1));
		assertEquals("foo", StringKit.getStringsubstr("foo", 3));
		assertEquals("foo", StringKit.getStringsubstr("foo", 5));
		assertEquals("f...", StringKit.getStringsubstr("foo", 1));
		assertEquals("...", StringKit.getStringsubstr("foo", 0));
		assertEquals("...", StringKit.getStringsubstr("foo", -1));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testIsNumber() {
		assertFalse(StringKit.isNumber(null));
		assertFalse(StringKit.isNumber(""));
		assertFalse(StringKit.isNumber("  1\t\n\r\b"));
		assertFalse(StringKit.isNumber("1.2"));
		assertFalse(StringKit.isNumber("-1"));
		assertFalse(StringKit.isNumber("1 1"));

		assertTrue(StringKit.isNumber("1234567890"));
	}

	@Test
	public void testIsBoolean(){
		assertFalse(StringKit.isBoolean(null));
		assertFalse(StringKit.isBoolean(""));
		assertFalse(StringKit.isBoolean(" true "));
		assertTrue(StringKit.isBoolean("true"));
		assertTrue(StringKit.isBoolean("false"));
	}

	@Test
	public void testToChineseNumber() {
		assertEquals("零", StringKit.toChineseNumber(0));

		assertEquals("一", StringKit.toChineseNumber(1));
		assertEquals("一十", StringKit.toChineseNumber(10));
		assertEquals("一百", StringKit.toChineseNumber(100));
		assertEquals("一千", StringKit.toChineseNumber(1000));
		assertEquals("一万", StringKit.toChineseNumber(10000));
		assertEquals("一亿", StringKit.toChineseNumber(100000000));
		assertEquals("二十一亿四千七百四十八万三千六百四十七", StringKit.toChineseNumber(Integer.MAX_VALUE));

		assertEquals("负一", StringKit.toChineseNumber(-1));
		assertEquals("负一十", StringKit.toChineseNumber(-10));
		assertEquals("负一百", StringKit.toChineseNumber(-100));
		assertEquals("负一千", StringKit.toChineseNumber(-1000));
		assertEquals("负一万", StringKit.toChineseNumber(-10000));
		assertEquals("负一亿", StringKit.toChineseNumber(-100000000));
		assertEquals("负二十一亿四千七百四十八万三千六百四十八", StringKit.toChineseNumber(Integer.MIN_VALUE));
	}

	@Test
	public void testCheckIsEnglish() {
		assertTrue(StringKit.checkIsEnglish(null));
		assertTrue(StringKit.checkIsEnglish(""));
		assertTrue(StringKit.checkIsEnglish(ALPHABET_LOWWERCASE));
		assertTrue(StringKit.checkIsEnglish(ALPHABET_UPPERCASE));
		assertTrue(StringKit.checkIsEnglish("foo-bar"));
		assertTrue(StringKit.checkIsEnglish("123"));
		assertFalse(StringKit.checkIsEnglish("-foo"));
		assertFalse(StringKit.checkIsEnglish("foo-"));

		String str = "(){}[]\",.<>\\/~!@#$%^&*;': ";
		for (String s : str.split("")) {
			assertFalse(StringKit.checkIsEnglish(s));
		}

		assertFalse(StringKit.checkIsEnglish("你好"));
	}

	@Test
	public void testIsChineseStr() {
		assertFalse(StringKit.isChineseStr(null));
		assertFalse(StringKit.isChineseStr(""));
		assertTrue(StringKit.isChineseStr("你好！"));
		assertTrue(StringKit.isChineseStr("foo好foo"));
		assertFalse(StringKit.isChineseStr("foo"));
	}

	@Test
	public void testIsLetter() {
		assertTrue(StringKit.isLetter(' '));
		assertTrue(StringKit.isLetter('a'));
		assertTrue(StringKit.isLetter('A'));
		assertTrue(StringKit.isLetter('-'));
		assertTrue(StringKit.isLetter('.'));
		assertFalse(StringKit.isLetter('한'));
		assertFalse(StringKit.isLetter('や'));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testFormatDecimalString() {
		assertEquals("1", StringKit.formatDecimalString("1"));
		assertEquals("111", StringKit.formatDecimalString("111"));
		assertEquals("1,111", StringKit.formatDecimalString("1111"));
		assertEquals("1,111,111", StringKit.formatDecimalString("1111111"));
		assertEquals("1,234,567,890", StringKit.formatDecimalString("1234567890"));

		assertEquals("-1", StringKit.formatDecimalString("-1"));
		assertEquals("-111", StringKit.formatDecimalString("-111"));
		assertEquals("-1,111", StringKit.formatDecimalString("-1111"));
		assertEquals("-1,111,111", StringKit.formatDecimalString("-1111111"));
		assertEquals("-1,234,567,890", StringKit.formatDecimalString("-1234567890"));
	}

	@Test
	public void testGetNumberFormat() {
		assertEquals("1", StringKit.getNumberFormat(1L));
		assertEquals("111", StringKit.getNumberFormat(111L));
		assertEquals("1,111", StringKit.getNumberFormat(1111L));
		assertEquals("1,111,111", StringKit.getNumberFormat(1111111L));
		assertEquals("1,234,567,890", StringKit.getNumberFormat(1234567890L));
		assertEquals("9,223,372,036,854,775,807", StringKit.getNumberFormat(Long.MAX_VALUE));

		assertEquals("-1", StringKit.getNumberFormat(-1L));
		assertEquals("-111", StringKit.getNumberFormat(-111L));
		assertEquals("-1,111", StringKit.getNumberFormat(-1111L));
		assertEquals("-1,111,111", StringKit.getNumberFormat(-1111111L));
		assertEquals("-1,234,567,890", StringKit.getNumberFormat(-1234567890L));
		assertEquals("-9,223,372,036,854,775,808", StringKit.getNumberFormat(Long.MIN_VALUE));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testStringFilter() {
		assertNull(StringKit.stringFilter(null));
		assertEquals("", StringKit.stringFilter(""));
		assertEquals("foo", StringKit.stringFilter("foo"));
		assertEquals("&nbsp;foo&nbsp;", StringKit.stringFilter(" foo "));
		assertEquals("&lt;foo&gt;", StringKit.stringFilter("<foo>"));
		assertEquals("&lt;&gt;&nbsp;&quot;&amp;&#47;&#92;\\'%", StringKit.stringFilter("<> \"&/\\'%"));
		assertEquals("", StringKit.stringFilter("\t\r\b"));
		assertEquals("foo\t\r\bfoo", StringKit.stringFilter("foo\t\r\bfoo"));
	}

	@Test
	public void testStringKeyWordFilter() {
		assertNull(StringKit.stringKeyWordFilter(null));
		assertEquals("", StringKit.stringKeyWordFilter(""));
		assertEquals("foo", StringKit.stringKeyWordFilter("foo"));
		assertEquals("foo", StringKit.stringKeyWordFilter(" foo "));
		assertEquals("foo", StringKit.stringKeyWordFilter("<foo>"));
		assertEquals("", StringKit.stringKeyWordFilter("<> \"&/\\'%"));
		assertEquals("", StringKit.stringKeyWordFilter("\t\r\b"));
		assertEquals("foo\t\r\bfoo", StringKit.stringKeyWordFilter("foo\t\r\bfoo"));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testGetRound() {
		assertEquals(0, StringKit.getRound(0));
		assertEquals(0, StringKit.getRound(0.4));
		assertEquals(0, StringKit.getRound(-0.4));
		assertEquals(1, StringKit.getRound(0.5));
		assertEquals(-1, StringKit.getRound(-0.5));
		assertEquals(Integer.MAX_VALUE, StringKit.getRound((double) Integer.MAX_VALUE + 0.4));
		assertEquals(Integer.MAX_VALUE, StringKit.getRound((double) Integer.MAX_VALUE - 0.5));
		assertEquals(Integer.MIN_VALUE, StringKit.getRound((double) Integer.MIN_VALUE + 0.5));
		assertEquals(Integer.MAX_VALUE, StringKit.getRound((double) Integer.MIN_VALUE - 0.6));
	}

	@Test
	public void testLength() {
		assertEquals(0, StringKit.length(null));
		assertEquals(0, StringKit.length(""));
		assertEquals(3, StringKit.length("foo"));
		assertEquals(5, StringKit.length(" foo "));
		assertEquals(5, StringKit.length("f,o.o"));
		assertEquals(3, StringKit.length("\t\r\b"));
		assertEquals(9, StringKit.length("<> \"&/\\'%"));
		assertEquals(4, StringKit.length("    "));
		assertEquals(4, StringKit.length("汉语"));
		assertEquals(6, StringKit.length("한국어"));
		assertEquals(8, StringKit.length("にほんご"));
	}

	@Test
	public void testGetChineseByStr() {
		assertNull(StringKit.getChineseByStr(null));
		assertEquals("", StringKit.getChineseByStr(""));
		assertEquals("", StringKit.getChineseByStr(" foo "));
		assertEquals("", StringKit.getChineseByStr("\t\r\b"));
		assertEquals("", StringKit.getChineseByStr("<> \"&/\\'%"));
		assertEquals("汉语我们的语言", StringKit.getChineseByStr("汉语——我们的语言。"));
		assertEquals("你好", StringKit.getChineseByStr("foo你bar好  !！"));
		assertEquals("", StringKit.getChineseByStr("한국어"));
		assertEquals("", StringKit.getChineseByStr("にほんご"));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testReplace() {
		assertNull(StringKit.replace(null, null, null));
		assertEquals("", StringKit.replace("", null, null));
		assertEquals("", StringKit.replace("", "", null));
		assertEquals("", StringKit.replace("", "", ""));
		assertEquals("foo", StringKit.replace("foo", "", "b"));
		assertEquals("f", StringKit.replace("foo", "o", ""));
		assertEquals("&lt;foo&lt;", StringKit.replace("<foo<", "<", "&lt;"));
		assertEquals("f2of2o2oo", StringKit.replace("foofooooo", "oo", "2o"));
		assertEquals("foo,foo", StringKit.replace("foo foo", " ", ","));
		assertEquals("|foo|||foo|", StringKit.replace(",foo,,,foo,", ",", "|"));
	}

	@Test
	public void testDeleteAny() {
		assertNull(StringKit.deleteAny(null, null));
		assertEquals("", StringKit.deleteAny("", null));
		assertEquals("", StringKit.deleteAny("", ""));
		assertEquals("foo", StringKit.deleteAny("foo", ""));
		assertEquals("foo", StringKit.deleteAny("foo", " ,"));
		assertEquals("foofoo", StringKit.deleteAny("foo, ,foo", " ,"));
		assertEquals("", StringKit.deleteAny("foo", "aof"));
		assertEquals("f", StringKit.deleteAny("foo", "Fo"));
	}

	@Test
	public void testDelimitedListToStringArray_StrStr() {
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.delimitedListToStringArray("foo", null)));
		assertTrue(Arrays.equals(new String[] { "f", "o", "o" }, StringKit.delimitedListToStringArray("foo", "")));
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.delimitedListToStringArray("foo", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.delimitedListToStringArray("a,b", ",")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.delimitedListToStringArray("a;b", ";")));
		assertTrue(Arrays.equals(new String[] { "a", ",", "b" }, StringKit.delimitedListToStringArray("a,b", "")));
	}

	@Test
	public void testDelimitedListToStringArray_StrStrStr() {
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.delimitedListToStringArray("foo", null, "o")));
		assertTrue(Arrays.equals(new String[] { "", "o", "o" }, StringKit.delimitedListToStringArray("foo", "", "f")));
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.delimitedListToStringArray("foo", SEPARATOR, "")));
		assertTrue(Arrays.equals(new String[] { "", "b" }, StringKit.delimitedListToStringArray("a,b", ",", "a")));
		assertTrue(
				Arrays.equals(new String[] { "a", "b" }, StringKit.delimitedListToStringArray("a;b", ";", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", "", "b" },
				StringKit.delimitedListToStringArray("a,b", "", SEPARATOR)));
	}

	// ---------------------------------------------------------------------
	private static final String[] MIXED_ARRAY_LIST = { null, "", "foo" };
	private static final Object[] MIXED_TYPE_LIST = { "foo", Integer.valueOf(100) };
	private static final String[] ARRAY_LIST = { "foo", "bar", "sun" };
	private static final String[] EMPTY_ARRAY_LIST = {};
	private static final String[] NULL_ARRAY_LIST = { null };
	private static final String TEXT_LIST = "foo,bar,sun";
	private static final String TEXT_LIST_NOSEP = "foobarsun";
	private static final String TEXT_LIST_NO_SEP = "foobarsun";
	private static final String SEPARATOR = ",";

	@Test
	public void testJoin_ArrayString() {
		assertNull(StringKit.join((Object[]) null, null));
		assertEquals(TEXT_LIST_NO_SEP, StringKit.join(ARRAY_LIST, null));
		assertEquals(TEXT_LIST_NO_SEP, StringKit.join(ARRAY_LIST, ""));
		assertEquals(TEXT_LIST, StringKit.join(ARRAY_LIST, SEPARATOR));
		assertEquals("", StringKit.join(NULL_ARRAY_LIST, null));

		assertEquals("", StringKit.join(EMPTY_ARRAY_LIST, null));
		assertEquals("", StringKit.join(EMPTY_ARRAY_LIST, ""));
		assertEquals("", StringKit.join(EMPTY_ARRAY_LIST, SEPARATOR));

		assertEquals(",,foo", StringKit.join(MIXED_ARRAY_LIST, SEPARATOR));
		assertEquals("foo,100", StringKit.join(MIXED_TYPE_LIST, SEPARATOR));
	}

	@Test
	public void testJoin_Strings() {
		assertNull(StringKit.join((String[]) null));
		assertEquals("foo", StringKit.join("f", "o", "o"));
		assertEquals("foo", StringKit.join(null, "", "foo"));
	}

	@Test
	public void testJoin_IterableString() {
		assertNull(StringKit.join((Iterable<?>) null, null));
		assertEquals(TEXT_LIST_NOSEP, StringKit.join(Arrays.asList(ARRAY_LIST), null));
		assertEquals(TEXT_LIST_NOSEP, StringKit.join(Arrays.asList(ARRAY_LIST), ""));
		assertEquals(TEXT_LIST, StringKit.join(Arrays.asList(ARRAY_LIST), SEPARATOR));
		assertEquals("", StringKit.join(Arrays.asList(NULL_ARRAY_LIST), null));

		assertEquals("", StringKit.join(Arrays.asList(EMPTY_ARRAY_LIST), null));
		assertEquals("", StringKit.join(Arrays.asList(EMPTY_ARRAY_LIST), ""));
		assertEquals("", StringKit.join(Arrays.asList(EMPTY_ARRAY_LIST), SEPARATOR));

		assertEquals("foo", StringKit.join(Collections.singleton("foo"), "x"));
		assertEquals("foo", StringKit.join(Collections.singleton("foo"), null));
	}

	@Test
	public void testJoin_IteratorString() {
		assertNull(StringKit.join((Iterator<?>) null, null));
		assertEquals(TEXT_LIST_NOSEP, StringKit.join(Arrays.asList(ARRAY_LIST).iterator(), null));
		assertEquals(TEXT_LIST_NOSEP, StringKit.join(Arrays.asList(ARRAY_LIST).iterator(), ""));
		assertEquals(TEXT_LIST, StringKit.join(Arrays.asList(ARRAY_LIST).iterator(), SEPARATOR));
		assertEquals("", StringKit.join(Arrays.asList(NULL_ARRAY_LIST).iterator(), null));

		assertEquals("", StringKit.join(Arrays.asList(EMPTY_ARRAY_LIST).iterator(), null));
		assertEquals("", StringKit.join(Arrays.asList(EMPTY_ARRAY_LIST).iterator(), ""));
		assertEquals("", StringKit.join(Arrays.asList(EMPTY_ARRAY_LIST).iterator(), SEPARATOR));

		assertEquals("foo", StringKit.join(Collections.singleton("foo").iterator(), "x"));
		assertEquals("foo", StringKit.join(Collections.singleton("foo").iterator(), null));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testSplit_StrInt() {
		assertNull(StringKit.split(null, 1));
		assertEquals(0, StringKit.split("", 1).size());
		assertNull(StringKit.split("abc", 0));
		assertNull(StringKit.split("abc", -1));

		String str = "a b";
		List<String> res = StringKit.split(str, 1);
		assertEquals(3, res.size());
		assertEquals("a", res.get(0));
		assertEquals(" ", res.get(1));
		assertEquals("b", res.get(2));

		str = "a..b";
		res = StringKit.split(str, 2);
		assertEquals(2, res.size());
		assertEquals("a.", res.get(0));
		assertEquals(".b", res.get(1));

		str = "a. b..c";
		res = StringKit.split(str, str.length());
		assertEquals(1, res.size());
		assertEquals(str, res.get(0));

		str = "abc";
		res = StringKit.split(str, 10);
		assertEquals(1, res.size());
		assertEquals(str, res.get(0));
	}

	@Test
	public void testSplit_Str() {
		assertNull(StringKit.split(null));
		assertEquals(0, StringKit.split("").length);
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.split(" foo ")));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ")));
	}

	@Test
	public void testSplit_StrChar() {
		assertNull(StringKit.split(null, ','));
		assertEquals(0, StringKit.split("", ',').length);
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ", ' ')));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split(",a,b,", ',')));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,,b", ',')));
		assertTrue(Arrays.equals(new String[] { "a b", "c" }, StringKit.split("a b,c", ',')));
		assertTrue(Arrays.equals(new String[] { "abc" }, StringKit.split("abc", ',')));
	}

	@Test
	public void testSplit_StrStr() {
		assertNull(StringKit.split(null, SEPARATOR));
		assertEquals(0, StringKit.split("", SEPARATOR).length);
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.split(" foo ", "")));
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.split(" foo ", null)));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.split(" foo ", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ", null)));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ", " ")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split(",a,b,", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,,b", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a b", "c" }, StringKit.split("a b,c", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,,b", ",,")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,b", ",,")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,b", ",,,")));
	}

	@Test
	public void testSplit_StrStrInt() {
		assertNull(StringKit.split(null, SEPARATOR, -1));
		assertEquals(0, StringKit.split("", SEPARATOR, -1).length);
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.split(" foo ", "", -1)));
		assertTrue(Arrays.equals(new String[] { "foo" }, StringKit.split(" foo ", null, -1)));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.split(" foo ", SEPARATOR, -1)));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ", null, -1)));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ", " ", -1)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split(",a,b,", SEPARATOR, -1)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,,b", SEPARATOR, -1)));
		assertTrue(Arrays.equals(new String[] { "a b", "c" }, StringKit.split("a b,c", SEPARATOR, -1)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,,b", ",,", -1)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,b", ",,", -1)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.split("a,,b", ",,,", -1)));

		assertTrue(Arrays.equals(new String[] { "f", "b  1.0 " }, StringKit.split("f b  1.0 ", " ", 2)));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.split("f b  1.0 ", " ", 5)));
		assertTrue(Arrays.equals(new String[] { "f b  1.0 " }, StringKit.split("f b  1.0 ", ".", 1)));
	}

	@Test
	public void testSplitNoCompress() {
		assertNull(StringKit.splitNoCompress(null, SEPARATOR));
		assertNull(StringKit.splitNoCompress("abc", null));
		assertTrue(Arrays.equals(new String[] { "abc" }, StringKit.splitNoCompress("abc", "")));
		assertEquals(0, StringKit.splitNoCompress("", SEPARATOR).length);
		assertTrue(Arrays.equals(new String[] { "", "foo", "" }, StringKit.splitNoCompress(" foo ", " ")));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0" }, StringKit.splitNoCompress("f b 1.0", " ")));
		assertTrue(Arrays.equals(new String[] { "a", "", "b" }, StringKit.splitNoCompress("a  b", " ")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitNoCompress("a  b", "  ")));
		assertTrue(Arrays.equals(new String[] { "a  b" }, StringKit.splitNoCompress("a  b", "   ")));
		assertTrue(Arrays.equals(new String[] { "", "a", "b", "" }, StringKit.splitNoCompress(",a,b,", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", ",b" }, StringKit.splitNoCompress("a,,,b", ",,")));
		assertTrue(Arrays.equals(new String[] { "a", ",b" }, StringKit.splitNoCompress("a, ,b", ", ")));
		assertTrue(Arrays.equals(new String[] { "a,", "b" }, StringKit.splitNoCompress("a, ,b", " ,")));
	}

	@Test
	public void testSplitc_StrStr() {
		assertNull(StringKit.splitc(null, SEPARATOR));
		assertTrue(Arrays.equals(new String[] { "" }, StringKit.splitc("", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", "")));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", (String) null)));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "f b  1.0 " }, StringKit.splitc("f b  1.0 ", (String) null)));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0", "" }, StringKit.splitc("f b  1.0 ", " ")));
		assertTrue(Arrays.equals(new String[] { "", "a", "b", "" }, StringKit.splitc(",a,b,", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,,b", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a b", "c" }, StringKit.splitc("a b,c", SEPARATOR)));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,,b", ",,")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,b", ",,")));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,b", ",,,")));
	}

	@Test
	public void testSplitc_StrChar() {
		assertNull(StringKit.splitc(null, SEPARATOR));
		assertTrue(Arrays.equals(new String[] { "" }, StringKit.splitc("", ',')));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", ',')));
		assertTrue(Arrays.equals(new String[] { "f", "b", "1.0", "" }, StringKit.splitc("f b  1.0 ", ' ')));
		assertTrue(Arrays.equals(new String[] { "", "a", "b", "" }, StringKit.splitc(",a,b,", ',')));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,,b", ',')));
		assertTrue(Arrays.equals(new String[] { "a b", "c" }, StringKit.splitc("a b,c", ',')));
	}

	@Test
	public void testSplitc_StrCharArray() {
		assertNull(StringKit.splitc(null, SEPARATOR));
		assertTrue(Arrays.equals(new String[] { "" }, StringKit.splitc("", SEPARATOR.toCharArray())));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", "".toCharArray())));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", (char[]) null)));
		assertTrue(Arrays.equals(new String[] { " foo " }, StringKit.splitc(" foo ", SEPARATOR.toCharArray())));
		assertTrue(Arrays.equals(new String[] { "f b  1.0 " }, StringKit.splitc("f b  1.0 ", (char[]) null)));
		assertTrue(
				Arrays.equals(new String[] { "f", "b", "1.0", "" }, StringKit.splitc("f b  1.0 ", " ".toCharArray())));
		assertTrue(
				Arrays.equals(new String[] { "", "a", "b", "" }, StringKit.splitc(",a,b,", SEPARATOR.toCharArray())));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,,b", SEPARATOR.toCharArray())));
		assertTrue(Arrays.equals(new String[] { "a b", "c" }, StringKit.splitc("a b,c", SEPARATOR.toCharArray())));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,,b", ",,".toCharArray())));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,b", ",,".toCharArray())));
		assertTrue(Arrays.equals(new String[] { "a", "b" }, StringKit.splitc("a,,b", ",,,".toCharArray())));
	}

	// ---------------------------------------------------------------------
	@Test
	public void testToString_StrListStr() {
		assertNull(StringKit.toString((List<String>) null, null));
		assertEquals(TEXT_LIST_NOSEP, StringKit.toString(Arrays.asList(ARRAY_LIST), null));
		assertEquals(TEXT_LIST_NOSEP, StringKit.toString(Arrays.asList(ARRAY_LIST), ""));
		assertEquals(TEXT_LIST, StringKit.toString(Arrays.asList(ARRAY_LIST), SEPARATOR));
		assertEquals("foo||bar||sun", StringKit.toString(Arrays.asList(ARRAY_LIST), "||"));
		assertEquals("", StringKit.toString(Arrays.asList(NULL_ARRAY_LIST), null));

		assertEquals("", StringKit.toString(Arrays.asList(EMPTY_ARRAY_LIST), null));
		assertEquals("", StringKit.toString(Arrays.asList(EMPTY_ARRAY_LIST), ""));
		assertEquals("", StringKit.toString(Arrays.asList(EMPTY_ARRAY_LIST), SEPARATOR));
	}

	@Test
	public void testToString_Object() {
		assertNull(StringKit.toString(null));
		assertEquals("", StringKit.toString(""));
		assertEquals("foo", StringKit.toString("foo"));
		assertEquals("foo 1.0", StringKit.toString("foo 1.0"));
	}

	@Test
	public void testToString_StrCollection() {
		assertNull(StringKit.toString((Collection<String>) null));
		assertEquals("", StringKit.toString(new HashSet<String>(Arrays.asList(NULL_ARRAY_LIST))));
		assertEquals("", StringKit.toString(new HashSet<String>(Arrays.asList(EMPTY_ARRAY_LIST))));
		assertEquals("foo", StringKit.toString(Collections.singleton("foo")));
	}

	@Test
	public void testToString_StrCollectionStr() {
		assertNull(StringKit.toString((Collection<String>) null, null));
		assertEquals("", StringKit.toString(new HashSet<String>(Arrays.asList(NULL_ARRAY_LIST)), null));
		assertEquals("", StringKit.toString(new HashSet<String>(Arrays.asList(EMPTY_ARRAY_LIST)), null));
		assertEquals("", StringKit.toString(new HashSet<String>(Arrays.asList(EMPTY_ARRAY_LIST)), ""));
		assertEquals("", StringKit.toString(new HashSet<String>(Arrays.asList(EMPTY_ARRAY_LIST)), SEPARATOR));

		assertEquals("foo", StringKit.toString(Collections.singleton("foo"), "x"));
		assertEquals("foo", StringKit.toString(Collections.singleton("foo"), null));
	}
}
