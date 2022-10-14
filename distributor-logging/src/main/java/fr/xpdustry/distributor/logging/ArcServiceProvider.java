package fr.xpdustry.distributor.logging;

import org.slf4j.*;
import org.slf4j.helpers.*;
import org.slf4j.spi.*;

@SuppressWarnings("NullAway.Init")
public final class ArcServiceProvider implements SLF4JServiceProvider {

  private ILoggerFactory loggerFactory;
  private IMarkerFactory markerFactory;
  private MDCAdapter mdcAdapter;

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return mdcAdapter;
  }

  @Override
  public String getRequestedApiVersion() {
    return "2.0.0";
  }

  @Override
  public void initialize() {
    loggerFactory = new ArcLoggerFactory();
    markerFactory = new BasicMarkerFactory();
    mdcAdapter = new NOPMDCAdapter();
  }
}
