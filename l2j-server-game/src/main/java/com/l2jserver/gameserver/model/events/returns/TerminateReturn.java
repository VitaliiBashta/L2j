package com.l2jserver.gameserver.model.events.returns;

public class TerminateReturn extends AbstractEventReturn {
  private final boolean terminate;

  public TerminateReturn(boolean terminate, boolean override, boolean abort) {
    super(override, abort);
    this.terminate = terminate;
  }

  public boolean terminate() {
    return terminate;
  }
}
