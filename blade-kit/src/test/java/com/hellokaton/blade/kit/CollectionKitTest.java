package com.hellokaton.blade.kit;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class CollectionKitTest {

  @Test
  public void isEmpty() {
    final Object[] array = {};
    Assert.assertTrue(CollectionKit.isEmpty(array));

    final Object[] array2 = {null};
    Assert.assertFalse(CollectionKit.isEmpty(array2));

    final Object[] array3 = null;
    Assert.assertTrue(CollectionKit.isEmpty(array3));

    final ArrayList collection = new ArrayList();
    Assert.assertTrue(CollectionKit.isEmpty(collection));

    final ArrayList collection2 = new ArrayList();
    collection2.add(null);
    Assert.assertFalse(CollectionKit.isEmpty(collection2));

    final ArrayList collection3 = null;
    Assert.assertTrue(CollectionKit.isEmpty(collection3));
  }

  @Test
  public void isNotEmpty() {
    final Object[] array = {};
    Assert.assertFalse(CollectionKit.isNotEmpty(array));

    final Object[] array2 = {null};
    Assert.assertTrue(CollectionKit.isNotEmpty(array2));

    final Object[] array3 = null;
    Assert.assertFalse(CollectionKit.isNotEmpty(array3));

    final ArrayList collection = new ArrayList();
    Assert.assertFalse(CollectionKit.isNotEmpty(collection));

    final ArrayList collection2 = new ArrayList();
    collection2.add(null);
    Assert.assertTrue(CollectionKit.isNotEmpty(collection2));

    final ArrayList collection3 = null;
    Assert.assertFalse(CollectionKit.isNotEmpty(collection3));
  }

  @Test
  public void newConcurrentMap() {
    Assert.assertNotNull(CollectionKit.newConcurrentMap(0));
    Assert.assertNotNull(CollectionKit.newConcurrentMap());
  }

  @Test
  public void newMap() {
    Assert.assertEquals(new HashMap(), CollectionKit.newMap());
  }

  @Test
  public void newLists() {
    final Object[] values = {};
    Assert.assertEquals(new ArrayList(), CollectionKit.newLists(values));
  }

  @Test
  public void newSets() {
    final Object[] values = {};
    Assert.assertEquals(new HashSet(), CollectionKit.newSets(values));
  }
}

