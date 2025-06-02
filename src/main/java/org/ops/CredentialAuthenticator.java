package org.ops;

public interface CredentialAuthenticator {
    boolean authenticate(Credential cred);
}
