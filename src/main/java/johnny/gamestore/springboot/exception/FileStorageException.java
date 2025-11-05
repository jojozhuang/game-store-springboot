/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.exception;

/** File Storage exception. */
public class FileStorageException extends RuntimeException {
  public FileStorageException(String message) {
    super(message);
  }

  public FileStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
