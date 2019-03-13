package com.blade.kit;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

public class CollectionKitTest {

  @Test
  public void isEmpty() {
    // Arrange
    final Object[] array = {};
    final Object[] array2 = {null};
    final ArrayList collection = new ArrayList();
    final ArrayList collection2 = new ArrayList();
    collection2.add(null);

    // Assert result
    Assert.assertTrue(CollectionKit.isEmpty(array));
    Assert.assertFalse(CollectionKit.isEmpty(array2));
    Assert.assertTrue(CollectionKit.isEmpty(collection));
    Assert.assertFalse(CollectionKit.isEmpty(collection2));
  }

  @Test
  public void isNotEmpty() {
    // Arrange
    final Object[] array = {};
    final Object[] array2 = {null};
    final ArrayList collection = new ArrayList();
    final ArrayList collection2 = new ArrayList();
    collection2.add(null);

    // Assert result
    Assert.assertFalse(CollectionKit.isNotEmpty(array));
    Assert.assertTrue(CollectionKit.isNotEmpty(array2));
    Assert.assertFalse(CollectionKit.isNotEmpty(collection));
    Assert.assertTrue(CollectionKit.isNotEmpty(collection2));
  }

  @Test
  public void newConcurrentMap()  {
    Assert.assertNotNull(CollectionKit.newConcurrentMap(0));
    Assert.assertNotNull(CollectionKit.newConcurrentMap());
  }

  @Test
  public void newLists() {
    // Arrange
    final Object[] values = {};

    // Assert result
    Assert.assertEquals(new ArrayList(), CollectionKit.newLists(values));
  }

  @Test
  public void newMap() {
    Assert.assertEquals(new HashMap(), CollectionKit.newMap());
  }

  @Test
  public void newSets() {
    // Arrange
    final Object[] values = {};

    // Assert result
    Assert.assertEquals(new HashSet(), CollectionKit.newSets());
  }
}
