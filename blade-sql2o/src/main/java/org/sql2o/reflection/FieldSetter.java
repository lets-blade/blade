package org.sql2o.reflection;

import org.sql2o.Sql2oException;

import java.lang.reflect.Field;

/**
 * used internally to set property values directly into the field. Only used if no setter method is found.
 */
public class FieldSetter implements Setter{

    private Field field;

    public FieldSetter(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public void setProperty(Object obj, Object value) {
        if (value == null && this.field.getType().isPrimitive()){
            return; // dont try set null to a primitive field
        }

        try {
            this.field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new Sql2oException("could not set field " + this.field.getName() + " on class " + obj.getClass().toString(), e);
        }
    }

    public Class getType() {
        return field.getType();
    }
}
