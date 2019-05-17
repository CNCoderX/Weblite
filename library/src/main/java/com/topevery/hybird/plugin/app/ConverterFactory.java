package com.topevery.hybird.plugin.app;

import java.text.ParseException;

/**
 * @author wujie
 */
public final class ConverterFactory {

    public static Converter create(String type) {
        Converter converter = null;
        switch (type) {
            case IntentData.TYPE_BOOLEAN:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        return Boolean.parseBoolean(source);
                    }
                };
                break;
            case IntentData.TYPE_BOOLEAN_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseBooleanArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_BYTE:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return Byte.parseByte(source);
                        } catch (NumberFormatException e) {
                            return (byte) 0;
                        }
                    }
                };
                break;
            case IntentData.TYPE_BYTE_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseByteArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_SHORT:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return Short.parseShort(source);
                        } catch (NumberFormatException e) {
                            return (short) 0;
                        }
                    }
                };
                break;
            case IntentData.TYPE_SHORT_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseShortArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_INTEGER:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return Integer.parseInt(source);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                };
                break;
            case IntentData.TYPE_INTEGER_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseIntArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_LONG:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return Long.parseLong(source);
                        } catch (NumberFormatException e) {
                            return 0L;
                        }
                    }
                };
                break;
            case IntentData.TYPE_LONG_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseLongArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_FLOAT:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return Float.parseFloat(source);
                        } catch (NumberFormatException e) {
                            return 0.0f;
                        }
                    }
                };
                break;
            case IntentData.TYPE_FLOAT_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseFloatArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_DOUBLE:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return Double.parseDouble(source);
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    }
                };
                break;
            case IntentData.TYPE_DOUBLE_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseDoubleArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
            case IntentData.TYPE_STRING:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        return source;
                    }
                };
                break;
            case IntentData.TYPE_STRING_ARRAY:
                converter = new Converter() {
                    @Override
                    public Object convert(String source) {
                        try {
                            return parseStringArray(source);
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                };
                break;
        }

        return converter;
    }

    static boolean[] parseBooleanArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        boolean[] array = new boolean[size];
        for (int i = 0; i < size; i++) {
            array[i] = Boolean.parseBoolean(members[i]);
        }
        return array;
    }

    static byte[] parseByteArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        byte[] array = new byte[size];
        for (int i = 0; i < size; i++) {
            try {
                array[i] = Byte.parseByte(members[i]);
            } catch (NumberFormatException e) {
                array[i] = (byte) 0;
            }
        }
        return array;
    }

    static short[] parseShortArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        short[] array = new short[size];
        for (int i = 0; i < size; i++) {
            try {
                array[i] = Short.parseShort(members[i]);
            } catch (NumberFormatException e) {
                array[i] = (short) 0;
            }
        }
        return array;
    }

    static int[] parseIntArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            try {
                array[i] = Integer.parseInt(members[i]);
            } catch (NumberFormatException e) {
                array[i] = 0;
            }
        }
        return array;
    }

    static long[] parseLongArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            try {
                array[i] = Long.parseLong(members[i]);
            } catch (NumberFormatException e) {
                array[i] = 0L;
            }
        }
        return array;
    }

    static float[] parseFloatArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        float[] array = new float[size];
        for (int i = 0; i < size; i++) {
            try {
                array[i] = Float.parseFloat(members[i]);
            } catch (NumberFormatException e) {
                array[i] = 0.0f;
            }
        }
        return array;
    }

    static double[] parseDoubleArray(String source) throws ParseException {
        String[] members = parseStringArray(source);
        if (members == null) return null;

        int size = members.length;
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            try {
                array[i] = Double.parseDouble(members[i]);
            } catch (NumberFormatException e) {
                array[i] = 0.0;
            }
        }
        return array;
    }

    static String[] parseStringArray(String source) throws ParseException {
        if (source == null) {
            return null;
        }

        source = source.trim();

        int length = source.length();
        if (source.charAt(0) != '[') {
            throw new ParseException("Parse intent-data array error.", 0);
        }
        if (source.charAt(length - 1) != ']') {
            throw new ParseException("Parse intent-data array error.", length - 1);
        }

        source = source.substring(1, length - 1);
        String[] members = source.split(",");

        int size = members.length;
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = members[i].trim();
        }
        return array;
    }
}
