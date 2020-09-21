package com.gladurbad.medusa.util;

import com.google.common.util.concurrent.AtomicDouble;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@UtilityClass
public class MathUtil {
    public long GCD_OFFSET;
    public final double EXPANDER = Math.pow(2, 24);

    public double offset(Vector from, Vector to) {
        from.setY(0);
        to.setY(0);

        return to.subtract(from).length();
    }

    public boolean playerMoved(Location from, Location to) {
        return playerMoved(from.toVector(), to.toVector());
    }

    public byte getByte(int num) {
        if (num > Byte.MAX_VALUE || num < Byte.MIN_VALUE) {
            throw new NumberFormatException("Integer " + num + " too large to cast to data format byte!"
                    + " (max=" + Byte.MAX_VALUE + " min=" + Byte.MIN_VALUE + ")");
        }

        return (byte) num;
    }

    public short getShort(int num) {
        if (num > Short.MAX_VALUE || num < Short.MIN_VALUE) {
            throw new NumberFormatException("Integer " + num + " too large to cast to data format short!"
                    + " (max=" + Short.MAX_VALUE + " min=" + Short.MIN_VALUE + ")");
        }
        return (short) num;
    }


    public boolean approxEquals(double accuracy, double equalTo, double... equals) {
        return Arrays.stream(equals).allMatch(equal -> MathUtil.getDelta(equalTo, equal) < accuracy);
    }

    public boolean approxEquals(double accuracy, int equalTo, int... equals) {
        return Arrays.stream(equals).allMatch(equal -> MathUtil.getDelta(equalTo, equal) < accuracy);
    }

    public boolean approxEquals(double accuracy, long equalTo, long... equals) {
        return Arrays.stream(equals).allMatch(equal -> MathUtil.getDelta(equalTo, equal) < accuracy);
    }


    //Returns -1 if fails.
    public <T extends Number> T tryParse(String string) {
        try {
            return (T) (Number) Double.parseDouble(string);
        } catch (NumberFormatException e) {

        }
        return (T) (Number) (-1);
    }

    //A lighter version of the Java hypotenuse function.
    public double hypot(double... value) {
        double total = 0;

        for (double val : value) {
            total += (val * val);
        }

        return Math.sqrt(total);
    }

    public float hypot(float... value) {
        float total = 0;

        for (float val : value) {
            total += (val * val);
        }

        return (float) Math.sqrt(total);
    }

    public double get3DDistance(Vector one, Vector two) {
        return hypot(one.getX() - two.getX(), one.getY() - two.getY(), one.getZ() - two.getZ());
    }

    public boolean playerMoved(Vector from, Vector to) {
        return from.distance(to) > 0;
    }

    public boolean playerLooked(Location from, Location to) {
        return (from.getYaw() - to.getYaw() != 0) || (from.getPitch() - to.getPitch() != 0);
    }

    public boolean elapsed(long time, long needed) {
        return Math.abs(System.currentTimeMillis() - time) >= needed;
    }

    //Euclid's algorithm
    public long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    //Euclid's algorithm
    public double gcd(long... input) {
        long result = input[0];
        for (int i = 1; i < input.length; i++) result = gcd(result, input[i]);
        return result;
    }

    //From Frequency - by Elevated/Gson
    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }


    public double gcf(long a, long b) {
        if (b == 0) {
            return a;
        } else {
            return (gcf(b, a % b));
        }
    }

    // Returns the absolute value of n-mid*mid*mid
     double diff(double n, double mid) {
        if (n > (mid * mid * mid))
            return (n - (mid * mid * mid));
        else
            return ((mid * mid * mid) - n);
    }

    // Returns cube root of a no n
    public double cbrt(double n) {
        // Set start and end for binary search
        double start = 0, end = n;

        // Set precision
        double e = 0.0000001;

        double mid = -1;
        double error = 1000;

        long ticks = 0;
        while (error > e) {
            mid = (start + end) / 2;
            error = diff(n, mid);

            // If error is less than e then mid is
            // our answer so return mid

            // If mid*mid*mid is greater than n set
            // end = mid
            if ((mid * mid * mid) > n)
                end = mid;

                // If mid*mid*mid is less than n set
                // start = mid
            else
                start = mid;

            if (error > e && ticks++ > 3E4) {
                return -1;
            }
        }
        return mid;
    }

    //A much lighter but very slightly less accurate Math.sqrt.
    public double sqrt(double number) {
        if (number == 0) return 0;
        double t;
        double squareRoot = number / 2;

        do {
            t = squareRoot;
            squareRoot = (t + (number / t)) / 2;
        } while ((t - squareRoot) != 0);

        return squareRoot;
    }

    public Vector getDirection(double yaw, double pitch) {
        Vector vector = new Vector();
        vector.setY(-Math.sin(Math.toRadians(pitch)));
        double xz = Math.cos(Math.toRadians(pitch));
        vector.setX(-xz * Math.sin(Math.toRadians(yaw)));
        vector.setZ(xz * Math.cos(Math.toRadians(yaw)));
        return vector;
    }

    public float sqrt(float number) {
        if (number == 0) return 0;
        float t;

        float squareRoot = number / 2;

        do {
            t = squareRoot;
            squareRoot = (t + (number / t)) / 2;
        } while ((t - squareRoot) != 0);

        return squareRoot;
    }

    public float normalizeAngle(float yaw) {
        return yaw % 360;
    }

    public double normalizeAngle(double yaw) {
        return yaw % 360;
    }

    public float getAngleDelta(float one, float two) {
        float delta = getDelta(one, two) % 360f;

        if (delta > 180) delta = 360 - delta;
        return delta;
    }

    //Euclid's algorithim
    public long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    //Euclid's algorithim
    public long lcm(long... input) {
        long result = input[0];
        for (int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    public float getDelta(float one, float two) {
        return Math.abs(one - two);
    }

    public double getDelta(double one, double two) {
        return Math.abs(one - two);
    }

    public long getDelta(long one, long two) {
        return Math.abs(one - two);
    }

    public long getDelta(int one, int two) {
        return Math.abs(one - two);
    }

    public long elapsed(long time) {
        return Math.abs(System.currentTimeMillis() - time);
    }

    public double getHorizontalDistance(Location from, Location to) {
        double deltaX = to.getX() - from.getX(), deltaZ = to.getZ() - from.getZ();
        return sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public double stdev(Collection<Double> list) {
        double sum = 0.0;
        double mean;
        double num = 0.0;
        double numi;
        double deno = 0.0;

        for (double i : list) {
            sum += i;
        }
        mean = sum / list.size();

        for (double i : list) {
            numi = Math.pow(i - mean, 2);
            num += numi;
        }

        return sqrt(num / list.size());
    }

    public int millisToTicks(long millis) {
        return (int) Math.ceil(millis / 50D);
    }

    public double getVerticalDistance(Location from, Location to) {
        return Math.abs(from.getY() - to.getY());
    }


    public double trim(int degree, double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.parseDouble(twoDForm.format(d).replaceAll(",", "."));
    }

    public float trimFloat(int degree, float d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Float.parseFloat(twoDForm.format(d).replaceAll(",", "."));
    }

    public double getYawDifference(Location one, Location two) {
        return Math.abs(one.getYaw() - two.getYaw());
    }

    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double round(double value, int places, RoundingMode mode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, mode);
        return bd.doubleValue();
    }

    public double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.UP);
        return bd.doubleValue();
    }

    public float round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public float round(float value, int places, RoundingMode mode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, mode);
        return bd.floatValue();
    }

    public float round(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.UP);
        return bd.floatValue();
    }

    public int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

    public double yawTo180D(double dub) {
        if ((dub %= 360.0) >= 180.0) {
            dub -= 360.0;
        }
        if (dub < -180.0) {
            dub += 360.0;
        }
        return dub;
    }

    public double getDirectionOld(Location from, Location to) {
        if (from == null || to == null) {
            return 0.0D;
        }
        double difX = to.getX() - from.getX();
        double difZ = to.getZ() - from.getZ();

        return (float) ((Math.atan2(difZ, difX) * 180.0D / Math.PI) - 90.0F);
    }

    public double getDirection(Location from, Location to) {
        if (from == null || to == null) {
            return 0.0;
        }
        double difX = to.getX() - from.getX();
        double difZ = to.getZ() - from.getZ();
        return MathUtil.yawTo180F((float) (Math.atan2(difZ, difX) * 180.0 / 3.141592653589793) - 90.0f);
    }

    public float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public float[] getRotations(LivingEntity origin, LivingEntity point) {
        Location two = point.getLocation(), one = origin.getLocation();
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public boolean isLookingTowardsEntity(Location from, Location to, LivingEntity entity) {
        float[] rotFrom = getRotations(from, entity.getLocation()), rotTo = getRotations(to, entity.getLocation());
        float deltaOne = getDelta(from.getYaw(), rotTo[0]), deltaTwo = getDelta(to.getYaw(), rotTo[1]);
        float offsetFrom = getDelta(yawTo180F(from.getYaw()), yawTo180F(rotFrom[0])), offsetTo = getDelta(yawTo180F(to.getYaw()), yawTo180F(rotTo[0]));

        return (deltaOne > deltaTwo && offsetTo > 15) || (MathUtil.getDelta(offsetFrom, offsetTo) < 1 && offsetTo < 10);
    }

    public double[] getOffsetFromEntity(Player player, LivingEntity entity) {
        double yawOffset = Math.abs(MathUtil.yawTo180F(player.getEyeLocation().getYaw()) - MathUtil.yawTo180F(MathUtil.getRotations(player.getLocation(), entity.getLocation())[0]));
        double pitchOffset = Math.abs(Math.abs(player.getEyeLocation().getPitch()) - Math.abs(MathUtil.getRotations(player.getLocation(), entity.getLocation())[1]));
        return new double[]{yawOffset, pitchOffset};
    }

    public double[] getOffsetFromLocation(Location one, Location two) {
        double yaw = MathUtil.getRotations(one, two)[0];
        double pitch = MathUtil.getRotations(one, two)[1];
        double yawOffset = Math.abs(yaw - MathUtil.yawTo180F(one.getYaw()));
        double pitchOffset = Math.abs(pitch - one.getPitch());
        return new double[]{yawOffset, pitchOffset};
    }

    public float getBaseSpeed(Player player) {
        return 0.34f + (PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public float getBaseGroundSpeed(Player player) {
        return 0.29f + (PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public float getBaseSpeed(Player player, float limit) {
        return limit + (PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * 0.062F) + ((player.getWalkSpeed() - 0.2F) * 1.6F);
    }

    //Not fully accurate bc mc coders like long numbers but whatever good enough.

    public double getStandardDeviation(long[] numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.length;
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public double get2DDistance(double x1, double z1, double x2, double z2) {
        return Math.sqrt((Math.abs(z2 - z1) * Math.abs(z2 - z1)) + (Math.abs(x2 - x1) * Math.abs(x2 - x1)));
    }

    public double getStandardDeviation(Deque<Float> numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.size();
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public double getStandardDeviation(Collection<Double> numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.size();
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public float getAngleDiff(float a, float b) {
        float diff = Math.abs(a - b);
        float altDiff = b + 360 - a;
        float altAltDiff = a + 360 - b;
        if (altDiff < diff) diff = altDiff;
        if (altAltDiff < diff) diff = altAltDiff;
        return diff;
    }

    public double getStandardDeviation(double[] numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.length;
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public double getKurtosis(Collection<Double> values) {
        double n = values.size();

        if (n < 3) return Double.NaN;

        double average = values.stream().mapToDouble(d -> d).average().getAsDouble();
        double deviation = getStandardDeviation(values.stream().mapToDouble(d -> d).toArray());

        AtomicDouble accum = new AtomicDouble(0D);

        values.forEach(d -> accum.getAndAdd(Math.pow(d.doubleValue() - average, 4D)));

        return n * (n + 1) / ((n - 1) * (n - 2) * (n - 3)) *
                (accum.get() / Math.pow(deviation, 4D)) - 3 *
                Math.pow(n - 1, 2D) / ((n - 2) * (n - 3));
    }

    public Vector getVectorForRotation(float pitch, float yaw) {
        float f = (float) Math.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = (float) Math.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = (float) -Math.cos(-pitch * 0.017453292F);
        float f3 = (float) Math.sin(-pitch * 0.017453292F);
        return new Vector((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public double getRotationSmoothness(float deltaRotation, float lastDeltaRotation) {
        return Math.abs(deltaRotation - lastDeltaRotation) * 0.5;
    }

    public double getDistanceBetweenAngles360(double angle1, double angle2) {
        double distance = Math.abs(angle1 % 360.0 - angle2 % 360.0);
        distance = Math.min(360.0 - distance, distance);
        return Math.abs(distance);
    }

    static {
        GCD_OFFSET = (long) Math.pow(2, 24);
    }

    public boolean isScientificNotation(double d) {
        return Double.toString(d).contains("E");
    }
}
