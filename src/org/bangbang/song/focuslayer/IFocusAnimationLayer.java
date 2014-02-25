package org.bangbang.song.focuslayer;

import android.view.View;
/**
 * 
 * <pre>
    +--------------focus layer (top layer)--------------------+
    |                                                         |
  +-+-----------subject layer (bottom layer)----------------+ |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | +-------------------------------------------------------+-+
  |                                                         |
  +---------------------------------------------------------+
  </pre>
*/
public interface IFocusAnimationLayer extends View.OnFocusChangeListener{
    public void onFocusSessionEnd(View lastFocus);
}
