import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentHalfPrimeCalculator {

    public static void main(String[] args) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (Integer prime : getPrimes(Integer.parseInt(args[0]))) {
//            System.out.print(prime + "\n");
            builder.append(prime).append("\n");
        }
        System.out.println(builder);
    }

    private static Integer[] getPrimes(int maxPrime) throws InterruptedException {

        List<Integer> numbers = new ArrayList<>(maxPrime / 2);
        numbers.add(2);
        for (int i = 3; i <= maxPrime; i += 2) {
            numbers.add(i);
        }


        ConcurrentSkipListSet<Integer> primeNumbers = new ConcurrentSkipListSet<>();
        CountDownLatch latch = new CountDownLatch(numbers.size());
        ExecutorService executors = Executors.newFixedThreadPool(Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS")));
//        synchronized (primeNumbers) {
        for (int i = 0; i < numbers.size(); i++) {
//            for (Integer candidate : numbers) {
            int candidate = numbers.get(i);
            int finalI = i;
            executors.submit(() -> {
//                    if (isPrime(numbers, candidate)) {
                if (isPrime(numbers.subList(0, finalI), candidate)) {
                    primeNumbers.add(candidate);
                }
                latch.countDown();
            });
        }
//        }
        latch.await();
        executors.shutdownNow();

        return primeNumbers.toArray(new Integer[0]);
    }

    private static boolean isPrime(List<Integer> primeNumbers, Integer candidate) {
//        for (Integer j : primeNumbers.subList(0, candidate - 2)) {
        for (Integer j : primeNumbers) {
            if (candidate % j == 0) {
                return false;
            }
        }
        return true;
    }
}
