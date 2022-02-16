package fr.mael.fk.utils;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import java.util.Random;

public class MathUtils {
    static public float nanoToSec = 1 / 1000000000f;

    // ---
    static public float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    static public float PI = 3.1415927f;
    static public float PI2 = MathUtils.PI * 2;

    static public float SQRT_3 = 1.73205f;

    static public float E = 2.7182818f;

    static private int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private int SIN_MASK = ~(-1 << MathUtils.SIN_BITS);
    static private int SIN_COUNT = MathUtils.SIN_MASK + 1;

    static private float radFull = MathUtils.PI * 2;
    static private float degFull = 360;
    static private float radToIndex = MathUtils.SIN_COUNT / MathUtils.radFull;
    static private float degToIndex = MathUtils.SIN_COUNT / MathUtils.degFull;

    /** multiply by this to convert from radians to degrees */
    static public float radiansToDegrees = 180f / MathUtils.PI;
    static public float radDeg = MathUtils.radiansToDegrees;
    /** multiply by this to convert from degrees to radians */
    static public float degreesToRadians = MathUtils.PI / 180;
    static public float degRad = MathUtils.degreesToRadians;

    static private class Sin {
        static float[] table = new float[MathUtils.SIN_COUNT];
        static {
            for (int i = 0; i < MathUtils.SIN_COUNT; i++) {
                Sin.table[i] = (float) Math.sin((i + 0.5f) / MathUtils.SIN_COUNT * MathUtils.radFull);
            }
            for (int i = 0; i < 360; i += 90) {
                Sin.table[(int) (i * MathUtils.degToIndex) & MathUtils.SIN_MASK] = (float) Math.sin(i * MathUtils.degreesToRadians);
            }
        }
    }

    /** Returns the sine in radians from a lookup table. */
    static public float sin(float radians) {
        return Sin.table[(int) (radians * MathUtils.radToIndex) & MathUtils.SIN_MASK];
    }

    /** Returns the cosine in radians from a lookup table. */
    static public float cos(float radians) {
        return Sin.table[(int) ((radians + MathUtils.PI / 2) * MathUtils.radToIndex) & MathUtils.SIN_MASK];
    }

    /** Returns the sine in radians from a lookup table. */
    static public float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * MathUtils.degToIndex) & MathUtils.SIN_MASK];
    }

    /** Returns the cosine in radians from a lookup table. */
    static public float cosDeg(float degrees) {
        return Sin.table[(int) ((degrees + 90) * MathUtils.degToIndex) & MathUtils.SIN_MASK];
    }

    // ---

    static private int ATAN2_BITS = 7; // Adjust for accuracy.
    static private int ATAN2_BITS2 = MathUtils.ATAN2_BITS << 1;
    static private int ATAN2_MASK = ~(-1 << MathUtils.ATAN2_BITS2);
    static private int ATAN2_COUNT = MathUtils.ATAN2_MASK + 1;
    static int ATAN2_DIM = (int) Math.sqrt(MathUtils.ATAN2_COUNT);
    static private float INV_ATAN2_DIM_MINUS_1 = 1.0f / (MathUtils.ATAN2_DIM - 1);

    static private class Atan2 {
        static float[] table = new float[MathUtils.ATAN2_COUNT];
        static {
            for (int i = 0; i < MathUtils.ATAN2_DIM; i++) {
                for (int j = 0; j < MathUtils.ATAN2_DIM; j++) {
                    float x0 = (float) i / MathUtils.ATAN2_DIM;
                    float y0 = (float) j / MathUtils.ATAN2_DIM;
                    Atan2.table[j * MathUtils.ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
                }
            }
        }
    }

    /** Returns atan2 in radians from a lookup table. */
    static public float atan2(float y, float x) {
        float add, mul;
        if (x < 0) {
            if (y < 0) {
                y = -y;
                mul = 1;
            } else {
                mul = -1;
            }
            x = -x;
            add = -MathUtils.PI;
        } else {
            if (y < 0) {
                y = -y;
                mul = -1;
            } else {
                mul = 1;
            }
            add = 0;
        }
        float invDiv = 1 / ((x < y ? y : x) * MathUtils.INV_ATAN2_DIM_MINUS_1);

        if (invDiv == Float.POSITIVE_INFINITY) { return ((float) Math.atan2(y, x) + add) * mul; }

        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.table[yi * MathUtils.ATAN2_DIM + xi] + add) * mul;
    }

    // ---

    static public Random random = new Random();

    /** Returns a random number between 0 (inclusive) and the specified value (inclusive). */
    static public int random(int range) {
        return MathUtils.random.nextInt(range + 1);
    }

    /** Returns a random number between start (inclusive) and end (inclusive). */
    static public int random(int start, int end) {
        return start + MathUtils.random.nextInt(end - start + 1);
    }

    /** Returns a random boolean value. */
    static public boolean randomBoolean() {
        return MathUtils.random.nextBoolean();
    }

    /** Returns true if a random value between 0 and 1 is less than the specified value. */
    static public boolean randomBoolean(float chance) {
        return MathUtils.random() < chance;
    }

    /** Returns random number between 0.0 (inclusive) and 1.0 (exclusive). */
    static public float random() {
        return MathUtils.random.nextFloat();
    }

    /** Returns a random number between 0 (inclusive) and the specified value (exclusive). */
    static public float random(float range) {
        return MathUtils.random.nextFloat() * range;
    }

    /** Returns a random number between start (inclusive) and end (exclusive). */
    static public float random(float start, float end) {
        return start + MathUtils.random.nextFloat() * (end - start);
    }

    // ---

    /** Returns the next power of two. Returns the specified value if the value is already a power of two. */
    static public int nextPowerOfTwo(int value) {
        if (value == 0) { return 1; }
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    static public boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    // ---

    static public int clamp(int value, int min, int max) {
        if (value < min) { return min; }
        if (value > max) { return max; }
        return value;
    }

    static public short clamp(short value, short min, short max) {
        if (value < min) { return min; }
        if (value > max) { return max; }
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if (value < min) { return min; }
        if (value > max) { return max; }
        return value;
    }

    // ---

    static private int BIG_ENOUGH_INT = 16 * 1024;
    static private double BIG_ENOUGH_FLOOR = MathUtils.BIG_ENOUGH_INT;
    static private double CEIL = 0.9999999;
    // static private double BIG_ENOUGH_CEIL = NumberUtils
    // .longBitsToDouble(NumberUtils.doubleToLongBits(BIG_ENOUGH_INT + 1) - 1);
    static private double BIG_ENOUGH_CEIL = 16384.999999999996;
    static private double BIG_ENOUGH_ROUND = MathUtils.BIG_ENOUGH_INT + 0.5f;

    /** Returns the largest integer less than or equal to the specified float. This method will only properly floor floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14). */
    static public int floor(float x) {
        return (int) (x + MathUtils.BIG_ENOUGH_FLOOR) - MathUtils.BIG_ENOUGH_INT;
    }

    /** Returns the largest integer less than or equal to the specified float. This method will only properly floor floats that are
     * positive. Note this method simply casts the float to int. */
    static public int floorPositive(float x) {
        return (int) x;
    }

    /** Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14). */
    static public int ceil(float x) {
        return (int) (x + MathUtils.BIG_ENOUGH_CEIL) - MathUtils.BIG_ENOUGH_INT;
    }

    /** Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil floats that
     * are positive. */
    static public int ceilPositive(float x) {
        return (int) (x + MathUtils.CEIL);
    }

    /** Returns the closest integer to the specified float. This method will only properly round floats from -(2^14) to
     * (Float.MAX_VALUE - 2^14). */
    static public int round(float x) {
        return (int) (x + MathUtils.BIG_ENOUGH_ROUND) - MathUtils.BIG_ENOUGH_INT;
    }

    /** Returns the closest integer to the specified float. This method will only properly round floats that are positive. */
    static public int roundPositive(float x) {
        return (int) (x + 0.5f);
    }

    /** Returns true if the value is zero (using the default tolerance as upper bound) */
    static public boolean isZero(float value) {
        return Math.abs(value) <= MathUtils.FLOAT_ROUNDING_ERROR;
    }

    /** Returns true if the value is zero.
     * @param tolerance represent an upper bound below which the value is considered zero. */
    static public boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /** Returns true if a is nearly equal to b. The function uses the default floating error tolerance.
     * @param a the first value.
     * @param b the second value. */
    static public boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= MathUtils.FLOAT_ROUNDING_ERROR;
    }

    /** Returns true if a is nearly equal to b.
     * @param a the first value.
     * @param b the second value.
     * @param tolerance represent an upper bound below which the two values are considered equal. */
    static public boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }
}
