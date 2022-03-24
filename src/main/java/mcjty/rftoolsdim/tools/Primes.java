package mcjty.rftoolsdim.tools;

import java.util.Random;

public class Primes {

    public static final long[] PRIMES = new long[] { 900157, 981961, 50001527, 32667413, 1111114993, 65548559, 320741, 100002509,
            35567897, 218021, 2900001163L, 3399018867L, 546151, 9890381, 666561271, 1666560437, 2556149, 64547713, 446455001329L, 246454942523L };

    private static final Random random = new Random();

    private int idx;

    public Primes() {
        this.idx = random.nextInt(PRIMES.length);
    }

    public int nextInt() {
        int rc = (int) PRIMES[idx++];
        if (idx >= PRIMES.length) {
            idx = 0;
        }
        return rc;
    }

    public int nextIntUnsigned() {
        int rc = (int) PRIMES[idx++];
        if (idx >= PRIMES.length) {
            idx = 0;
        }
        if (rc < 0) {
             rc = -rc;
        }
        return rc;
    }

    public long nextLong() {
        long rc = PRIMES[idx++];
        if (idx >= PRIMES.length) {
            idx = 0;
        }
        return rc;
    }
}
