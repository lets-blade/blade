package org.sql2o.reflection;

import org.sql2o.Sql2oException;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("Unsafe")
public class UnsafeFieldSetterFactory implements FieldSetterFactory, ObjectConstructorFactory {
    private final static Unsafe theUnsafe;
    static {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field declaredField = unsafeClass.getDeclaredField("theUnsafe");
            declaredField.setAccessible(true);
            theUnsafe = (Unsafe) declaredField.get(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Setter newSetter(final Field field) {
        final Class type = field.getType();
        final boolean isStatic = Modifier.isStatic(field.getModifiers());

        final long offset =  isStatic
                ? theUnsafe.staticFieldOffset(field)
                : theUnsafe.objectFieldOffset(field);

        if (!Modifier.isVolatile(field.getModifiers())) {
            if (type == Boolean.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putBoolean(obj, offset, (Boolean) value);
                    }

                    public Class getType() {
                        return Boolean.TYPE;
                    }
                };
            }
            if (type == Character.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putChar(obj, offset, (Character) value);
                    }

                    public Class getType() {
                        return Character.TYPE;
                    }
                };
            }
            if (type == Byte.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putByte(obj, offset, ((Number) value).byteValue());
                    }

                    public Class getType() {
                        return Byte.TYPE;
                    }
                };
            }
            if (type == Short.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putShort(obj, offset, ((Number) value).shortValue());
                    }

                    public Class getType() {
                        return Short.TYPE;
                    }
                };
            }
            if (type == Integer.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putInt(obj, offset, ((Number) value).intValue());
                    }

                    public Class getType() {
                        return Integer.TYPE;
                    }
                };
            }
            if (type == Long.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putLong(obj, offset, ((Number) value).longValue());
                    }

                    public Class getType() {
                        return Long.TYPE;
                    }
                };
            }
            if (type == Float.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putFloat(obj, offset, ((Number) value).floatValue());
                    }

                    public Class getType() {
                        return Float.TYPE;
                    }
                };
            }
            if (type == Double.TYPE) {
                return new Setter() {
                    public void setProperty(Object obj, Object value) {
                        if (value == null) return;
                        theUnsafe.putDouble(obj, offset, ((Number) value).doubleValue());
                    }

                    public Class getType() {
                        return Double.TYPE;
                    }
                };
            }
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    theUnsafe.putObject(obj, offset, value);
                }

                public Class getType() {
                    return type;
                }
            };
        }

        if (type == Boolean.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putBooleanVolatile(obj, offset, (Boolean) value);
                }

                public Class getType() {
                    return Boolean.TYPE;
                }
            };
        }
        if (type == Character.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putCharVolatile(obj, offset, (Character) value);
                }

                public Class getType() {
                    return Character.TYPE;
                }
            };
        }
        if (type == Byte.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putByteVolatile(obj, offset, ((Number) value).byteValue());
                }

                public Class getType() {
                    return Byte.TYPE;
                }
            };
        }
        if (type == Short.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putShortVolatile(obj, offset, ((Number) value).shortValue());
                }

                public Class getType() {
                    return Short.TYPE;
                }
            };
        }
        if (type == Integer.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putIntVolatile(obj, offset, ((Number) value).intValue());
                }

                public Class getType() {
                    return Integer.TYPE;
                }
            };
        }
        if (type == Long.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putLongVolatile(obj, offset, ((Number) value).longValue());
                }

                public Class getType() {
                    return Long.TYPE;
                }
            };
        }
        if (type == Float.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putFloatVolatile(obj, offset, ((Number) value).floatValue());
                }

                public Class getType() {
                    return Float.TYPE;
                }
            };
        }
        if (type == Double.TYPE) {
            return new Setter() {
                public void setProperty(Object obj, Object value) {
                    if (value == null) return;
                    theUnsafe.putDoubleVolatile(obj, offset, ((Number) value).doubleValue());
                }

                public Class getType() {
                    return Double.TYPE;
                }
            };
        }
        return new Setter() {
            public void setProperty(Object obj, Object value) {
                theUnsafe.putObjectVolatile(obj, offset, value);
            }

            public Class getType() {
                return type;
            }
        };
    }

    public ObjectConstructor newConstructor(final Class<?> clazz) {
        return getConstructor(clazz);
    }
    public static ObjectConstructor getConstructor(final Class<?> clazz) {
        return new ObjectConstructor() {
            public Object newInstance() {
                try {
                    return theUnsafe.allocateInstance(clazz);
                } catch (InstantiationException e) {
                    throw new Sql2oException("Could not create a new instance of class " + clazz, e);
                }
            }
        };
    }
}
