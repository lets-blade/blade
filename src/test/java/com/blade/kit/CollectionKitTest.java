package com.blade.kit;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

public class CollectionKitTest {

  @Test
  public void isEmpty() {
    final Object[] array = {};
    assertTrue(CollectionKit.isEmpty(array));

    final Object[] array2 = {null};
    assertFalse(CollectionKit.isEmpty(array2));

    final Object[] array3 = null;
    assertTrue(CollectionKit.isEmpty(array3));

    final ArrayList collection = new ArrayList();
    assertTrue(CollectionKit.isEmpty(collection));

    final ArrayList collection2 = new ArrayList();
    collection2.add(null);
    assertFalse(CollectionKit.isEmpty(collection2));

    final ArrayList collection3 = null;
    assertTrue(CollectionKit.isEmpty(collection3));
  }

  @Test
  public void isNotEmpty() {
    final Object[] array = {};
    assertFalse(CollectionKit.isNotEmpty(array));

    final Object[] array2 = {null};
    assertTrue(CollectionKit.isNotEmpty(array2));

    final Object[] array3 = null;
    assertFalse(CollectionKit.isNotEmpty(array3));

    final ArrayList collection = new ArrayList();
    assertFalse(CollectionKit.isNotEmpty(collection));

    final ArrayList collection2 = new ArrayList();
    collection2.add(null);
    assertTrue(CollectionKit.isNotEmpty(collection2));

    final ArrayList collection3 = null;
    assertFalse(CollectionKit.isNotEmpty(collection3));
  }

  @Test
  public void newConcurrentMap() {
    assertNotNull(CollectionKit.newConcurrentMap(0));
    assertNotNull(CollectionKit.newConcurrentMap());
  }

  @Test
  public void newMap() {
    assertEquals(new HashMap(), CollectionKit.newMap());
  }

  @Test
  public void newLists() {
    final Object[] values = {};
    assertEquals(new ArrayList(), CollectionKit.newLists(values));
  }

  @Test
  public void newSets() {
    final Object[] values = {};
    assertEquals(new HashSet(), CollectionKit.newSets(values));
  }
}

