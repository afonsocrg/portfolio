package sth.exceptions;

/**
 * Exception for invalid Survey closure
 */
public class InvalidCancelException extends Exception {

  /** Class serial number. */
  private static final long serialVersionUID = 201409301048L;

  /** Exception origin */
  private String _origin;

  /**
   * @param origin
   */
  public InvalidCancelException(String origin) {
    _origin = origin;
  }


  /**
   * @return the exception origin
   */
  public String getOrigin() {
    return _origin;
  }

}
