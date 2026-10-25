package com.nikita;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class DecodingFile {
    public static void main(String[] args) throws IOException {
        Path input = Paths.get("C:\\Users\\Nikita\\IdeaProjects\\RSA\\src\\main\\java\\com\\nikita\\files\\coding1.txt");
        Path privateKey = Paths.get("keys/private.txt");
        Path output = Paths.get("C:\\Users\\Nikita\\IdeaProjects\\RSA\\src\\main\\java\\com\\nikita\\files\\restored1.txt");
        decryptFile(input, privateKey, output);
    }

    private static int[] readPrivateKey(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.size() != 2 || !lines.get(0).startsWith("n=") || !lines.get(1).startsWith("d=")) {
            throw new IOException("Private key must contain two lines: n=... and d=...");
        }

        int n = Integer.parseInt(lines.get(0).substring(2).trim());
        int d = Integer.parseInt(lines.get(1).substring(2).trim());
        if (n <= 255 || d <= 1 || d >= n) {
            throw new IOException("For this program, n must be > 255 and 1 < d < n.");
        }

        return new int[] {n, d};
    }

    private static int modPow(int value, int exponent, int modulus) {
        long result = 1;
        long base = value % modulus;
        int power = exponent;

        while (power > 0) {
            if (power % 2 == 1) {
                result = result * base % modulus;
            }
            base = base * base % modulus;
            power /= 2;
        }

        return (int) result;
    }

    private static void decryptFile(Path inputPath, Path privateKeyPath, Path outputPath) throws IOException {
        int[] key = readPrivateKey(privateKeyPath);
        int n = key[0];
        int d = key[1];
        long count = 0;

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
             OutputStream output = new BufferedOutputStream(Files.newOutputStream(
                     outputPath, StandardOpenOption.CREATE_NEW))) {
            String line;

            while ((line = reader.readLine()) != null) {
                int encrypted = Integer.parseInt(line.trim());
                if (encrypted < 0 || encrypted >= n) {
                    throw new IOException("Encrypted block must be in range 0..n-1.");
                }

                int value = modPow(encrypted, d, n);
                if (value < 0 || value > 255) {
                    throw new IOException("Decrypted value does not fit in a byte. Check the key and file.");
                }

                output.write(value);
                count++;
            }
        }

        System.out.println("Decrypted bytes: " + count);
        System.out.println("Restored file: " + outputPath.toAbsolutePath());
    }
}