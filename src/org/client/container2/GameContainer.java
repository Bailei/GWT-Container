package org.client.container2;

import com.google.gwt.json.client.*;

public class GameContainer {  
  private final GameServer gameServer = new GameServer();
  //private String myPlayerId = "5685265389584384";
  //private String accessSignature = "7b72415da6285972c675ff5a39e7f691";
  
  private String myPlayerId = "5757334940811264";
  private String accessSignature = "1d99145115e1c57fd4fecbdab3903086";
	
  public GameContainer() {
  }
  
  public void sendEnterQueue(JSONString gameId) {
    gameServer.sendEnterQueue(
    		this, 
    		new JSONString(accessSignature),
    		new JSONString(myPlayerId),
    		gameId); 
  }
  
  public void sendMakeMove(JSONArray move) {
	//test("send make move in container");
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
