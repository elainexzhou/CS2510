// Assignment 8
// Jiadong Lou
// jiadonglou
// Zhou Xiaolu
// elaine07
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.Tester;

// A class that defines a new permutation code, as well as methods for encoding
// and decoding of the messages that use this code.

public class PermutationCode {
    // The original list of characters to be encoded
    ArrayList<Character> alphabet = 
            new ArrayList<Character>(Arrays.asList(
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
                        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
                        't', 'u', 'v', 'w', 'x', 'y', 'z'));

    ArrayList<Character> code = new ArrayList<Character>(26);

    // A random number generator
    Random rand = new Random();

    // Create a new instance of the encoder/decoder with a new permutation code
    PermutationCode() {
        this.code = this.initEncoder();
    }

    // Create a new instance of the encoder/decoder with the given code
    PermutationCode(ArrayList<Character> code) {
        this.code = code;
    }

    // Initialize the encoding permutation of the characters
    ArrayList<Character> initEncoder() {
        
        ArrayList<Character> copy =
                new ArrayList<Character>(Arrays.asList(
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
                        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
                        't', 'u', 'v', 'w', 'x', 'y', 'z'));
        
        return this.initEncoderHelper(new ArrayList<Character>(26), copy);
    }

    // helper function for initEncoder()
    ArrayList<Character> initEncoderHelper(ArrayList<Character> code, ArrayList<Character> left) {
        if (left.size() == 0) {
            return code;
        } 
        else {
            int randIndex = this.rand.nextInt(left.size());
            code.add(left.get(randIndex));
            left.remove(randIndex);
            return this.initEncoderHelper(code, left);
        }
    }

    // produce an encoded <code>String</code> from the given <code>String</code>
    String encode(String source) {
        return this.encodeHelper(source, "");
    }

    // helper function for encode()
    String encodeHelper(String source, String acc) {
        if (source.equals("")) {
            return acc;
        } 
        else {
            Character first = source.charAt(0);
            String rest = source.substring(1);

            if (this.alphabet.contains(first)) {
                acc = acc.concat(this.code.get(this.alphabet.indexOf(first)).toString());
            } 
            else {
                acc = acc.concat(first.toString());
            }
            return this.encodeHelper(rest, acc);
        }
    }

    // produce an decoded <code>String</code> from the given <code>String</code>
    String decode(String code) {
        return this.decodeHelper(code, "");
    }

    // helper function for decode()
    String decodeHelper(String code, String m) {
        if (code.equals("")) {
            return m;
        } 
        else {
            Character first = code.charAt(0);
            String rest = code.substring(1);

            if (this.alphabet.contains(first)) {
                m = m.concat(this.alphabet.get(this.code.indexOf(first)).toString());
            }
            else {
                m = m.concat(first.toString());
            }
            return this.decodeHelper(rest, m);

        }
    }
}

// Example for permutationcode
class ExamplesPermutation {
    ArrayList<Character> code = new ArrayList<Character>(Arrays.asList(
            'd', 'w', 'z', 'y', 'm', 'q', 'r', 'l', 'e', 'f', 
            'n', 'b', 'u', 'g', 'a', 'x', 'c', 'h', 'k', 
            'i', 'p', 'j', 'v', 's', 't', 'o'));

    String decoded1 = "hello world";
    String encoded1 = "lmbba vahby";

    String decoded2 = "jayden!?;";
    String encoded2 = "fdtymg!?;";

    PermutationCode p = new PermutationCode(this.code);

    // test for encode()
    void testEncode(Tester t) {
        t.checkExpect(p.encode(this.decoded1), this.encoded1);
        t.checkExpect(p.encode(this.decoded2), this.encoded2);
    }

    // test for decode()
    void testDecode(Tester t) {
        t.checkExpect(p.decode(this.encoded1), this.decoded1);
        t.checkExpect(p.decode(this.encoded2), this.decoded2);
    }
}