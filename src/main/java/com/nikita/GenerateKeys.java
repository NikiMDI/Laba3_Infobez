package com.nikita;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class GenerateKeys {
    public static void main(String[] args) throws IOException {
        Random random = new Random();

        int start = 17;
        int end = 1000;
        int number = start + random.nextInt(end - start + 1);

        int p = nextPrime(number);
        int q = nextPrime(p + 1);

        int n = p * q;
        int phi = (p - 1) * (q - 1);

        int e = findE(phi, random);
        int d = findD(e, phi);

        System.out.println("number = " + number);
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("n = " + n);
        System.out.println("phi = " + phi);
        System.out.println("e = " + e);
        System.out.println("d = " + d);
        saveKeys(n, e, d);
    }

    private static boolean isPrime(int x){
        if (x < 2){
            return false;
        }
        for (int i = 2; i < x - 1; i++){
            if (x % i == 0){
                return false;
            }
        }
        return true;
    }

    private static int nextPrime(int from){
        int candidate = from;
        while (!isPrime(candidate)){
            candidate++;
        }
        return candidate;
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int remainder = a % b;
            a = b;
            b = remainder;
        }
        return a;
    }

    private static int findE(int phi, Random random) {
        int e;
        do {
            e = 2 + random.nextInt(phi - 2);
        } while (gcd(e, phi) != 1);
        return e;
    }

    private static int findD(int e, int phi) {
        for (int d = 2; d < phi; d++) {
            if ((long) e * d % phi == 1) {
                return d;
            }
        }
        throw new IllegalStateException("Cannot find d");
    }

    private static void saveKeys(int n, int e, int d) throws IOException {
        Path directory = Paths.get("keys");
        Files.createDirectories(directory);

        Path publicPath = directory.resolve("public.txt");
        Path privatePath = directory.resolve("private.txt");

        String publicKey = "n=" + n + "\ne=" + e + "\n";
        String privateKey = "n=" + n + "\nd=" + d + "\n";

        Files.write(publicPath, publicKey.getBytes(StandardCharsets.UTF_8));
        Files.write(privatePath, privateKey.getBytes(StandardCharsets.UTF_8));

        System.out.println("Public key file: " + publicPath.toAbsolutePath());
        System.out.println("Private key file: " + privatePath.toAbsolutePath());
    }
}