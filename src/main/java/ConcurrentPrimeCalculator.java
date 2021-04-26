import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConcurrentPrimeCalculator {

    public static void main(String[] args) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (Integer prime : getPrimes(Integer.parseInt(args[0]))) {
//            System.out.print(prime + "\n");
            builder.append(prime).append("\n");
        }
        System.out.println(builder);
    }

    private static Integer[] getPrimes(int maxPrime) throws InterruptedException {
        List<Integer> numbers = Stream.generate(new Supplier<Integer>() {
            int i = 2;

            @Override
            public Integer get() {
                return i++;
            }
        }).limit(maxPrime).collect(Collectors.toList());

        ConcurrentSkipListSet<Integer> primeNumbers = new ConcurrentSkipListSet<>();
        CountDownLatch latch = new CountDownLatch(numbers.size());
        ExecutorService executors = Executors.newFixedThreadPool(Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS")));
        synchronized (primeNumbers) {
            for (Integer candidate : numbers) {
                executors.submit(() -> {
                    if (isPrime(numbers, candidate)) {
                        primeNumbers.add(candidate);
                    }
                    latch.countDown();
                });
            }
        }
        latch.await();
        executors.shutdownNow();

        return primeNumbers.toArray(new Integer[0]);
    }

    private static boolean isPrime(List<Integer> primeNumbers, Integer candidate) {
        for (Integer j : primeNumbers.subList(0, candidate - 2)) {
            if (candidate % j == 0) {
                return false;
            }
        }
        return true;
    }
}
