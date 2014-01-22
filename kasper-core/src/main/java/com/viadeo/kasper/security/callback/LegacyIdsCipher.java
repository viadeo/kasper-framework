package com.viadeo.kasper.security.callback;

/**
 * Capability to cipher (encrypt and decrypt) ids from requests (Query or Command)
 */
public interface LegacyIdsCipher {
    int encrypt(int id);

    int decrypt(int id);
}
