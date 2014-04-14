package org.client.container2;

import com.google.gwt.json.client.*;
import com.google.gwt.user.client.ui.Label;

public class GameContainer {
    
  //private final GameServer gameServer = new GameServer();
  
  private final GameServer gameServer;
 
  
  private String myPlayerId = "6022506221666304";
  private String accessSignature = "e574f5726c00421bec1cfd05e7c5af5";
	
  public GameContainer(Label countdown) {
      gameServer = new GameServer(countdown);
  }
  
  
  
  public void sendEnterQueue(JSONString gameId) {
    gameServer.sendEnterQueue(
    		this, 
    		new JSONString(accessSignature),
    		new JSONString(myPlayerId),
    		gameId); 
  }
  
  public void sendMakeMove(JSONArray move) {
   	gameServer.sendMakeMove(move);
  }
  
  public void closeSocket() {
    gameServer.closeSocket();
  }
  
  public void updateUi(JSONObject state, JSONArray lastMove, JSONString lastMovePlayerId, JSONArray playerIds) {
    JSONObject message = new JSONObject();
    message.put("state", state);
    message.put("lastMove", lastMove);
    message.put("lastMovePlayerId", lastMovePlayerId);
    message.put("playerIds", playerIds);
    sendMessage(message.toString());
  }
  
  public native void sendMessage(String message) /*-{
    var iframe = $doc.getElementById("child");
    iframe.contentWindow.postMessage(message, "*");
  }-*/;
  
  //Just for test
  private native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}
