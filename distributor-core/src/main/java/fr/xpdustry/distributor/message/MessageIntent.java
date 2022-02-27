package fr.xpdustry.distributor.message;

/**
 * Enum containing generic message intent for message formatting.
 */
public enum   MessageIntent {

  /**
   * This intent requires the sender to NOT apply special formatting.
   */
  NONE,

  /**
   * This intent is for debugging messages.
   */
  DEBUG,

  /**
   * This intent is for any normal message.
   */
  INFO,

  /**
   * This intent is for errors and warnings.
   */
  ERROR,

  /**
   * This intent is for success messages.
   */
  SUCCESS,

  /**
   * THis intent is ofr system or server-wide messages.
   */
  SYSTEM
}
