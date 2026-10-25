package com.nikita;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CodingFile {
    public static void main(String[] args) throws IOException {
        Path input = Paths.get("C:\\Users\\Nikita\\IdeaProjects\\RSA\\src\\main\\java\\com\\nikita\\files\\example.txt");
        Path publicKey = Paths.get("keys/public.txt");
        Path output = Paths.get("C:\\Users\\Nikita\\IdeaProjects\\RSA\\src\\main\\java\\com\\nikita\\files\\coding1.txt");
        codingFile(input, publicKey, output);
    }

    private static int[] readPublicKey(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.size() != 2 || !lines.get(0).startsWith("n=") || !lines.get(1).startsWith("e=")) {
            throw new IOException("Public key must contain two lines: n=... and e=...");
        }

        int n = Integer.parseInt(lines.get(0).substring(2).trim());
        int e = Integer.parseInt(lines.get(1).substring(2).trim());
        if (n <= 255 || e <= 1 || e >= n) {
            throw new IOException("For this program, n must be > 255 and 1 < e < n.");
        }

        return new int[] {n, e};
    }

    private static int modPow(int value, int exponent, int modulus) {
        long result = 1;
        long base = value % modulus;
        int power = exponent;

        while (power > 0) {
            if (power % 2 == 1){
                result = result * base % modulus;
            }
            base = base * base % modulus;
            power /= 2;
        }
        return (int) result;
    }

    private static void codingFile(Path inputPath, Path publicKeyPath, Path outputPath)
            throws IOException {
        int[] key = readPublicKey(publicKeyPath);
        int n = key[0];
        int e = key[1];
        long count = 0;

        try (InputStream input = new BufferedInputStream(Files.newInputStream(inputPath));
             BufferedWriter writer = Files.newBufferedWriter(outputPath,
                     StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            int value;

            while ((value = input.read()) != -1) {
                int encrypted = modPow(value, e, n);
                writer.write(Integer.toString(encrypted));
                writer.newLine();
                count++;
            }
        }

        System.out.println("Encrypted bytes: " + count);
        System.out.println("Encrypted file: " + outputPath.toAbsolutePath());
    }
}