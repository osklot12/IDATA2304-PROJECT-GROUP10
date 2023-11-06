package no.ntnu.tools;

/**
 * A logger class for encapsulating all the logging. We can either reduce the number of SonarLint
 * warnings, or implement it properly. This class makes sure we sue the same logging in all
 * places of our code.
 */
public class Logger {
  /**
   * Not allowed to create an instance of this class.
   */
  private Logger() {
  }

  /**
   * Log an information message.
   *
   * @param message The message to log. A newline is appended automatically.
   */
  public static void info(String message) {
    System.out.println(message);
  }

  /**
   * Log an info message without appending a newline to the log.
   *
   * @param message The message to log
   */
  public static void infoNoNewline(String message) {
    System.out.print(message);
  }

  /**
   * Log an error message.
   *
   * @param message The error message to log
   */
  public static void error(String message) {
    System.err.println(message);
  }

  /**
   * Log an array of bytes.
   *
   * @param bytes array of bytes to log
   */
  public static void printBytes(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    int n = 16; // bytes per line

    int byteCounter = 1;
    for (byte b : bytes) {
      String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
      sb.append(binaryString).append(" ");

      // creates a new line every n bytes
      if (byteCounter % n == 0) {
        sb.append(System.lineSeparator());
      }
      byteCounter++;
    }

    System.out.println(sb.toString().trim());
  }
}
