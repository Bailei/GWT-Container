package org.client.container1;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.json.client.*;

public class GameContainer {  
	
  private final GameServer gameServer = new GameServer();
  private String accessSignature = "80b8757f2a9aaf21fc759030414e3085";
  private String myPlayerId = "6400757548974080";
	
  public GameContainer() {
  }
  
  public void insertNewMatch(String gameId, List<String> playerIds) {
    gameServer.sendInsertNewMatch(accessSignature, gameId, playerIds, this, myPlayerId);
  }
  
  public void sendMakeMove(JSONArray move) {
   	gameServer.sendMakeMove(move);
  }
  
  public void updateGame() {
    gameServer.getMatchInfo();
  }
  
  public void updateUi(JSONObject state, JSONArray lastMove, JSONString lastMovePlayerId) {
	/**
	 * 
	 */
	test("tiger");
	
    JSONObject message = new JSONObject();
    message.put("state", state);
    message.put("lastMove", lastMove);
    message.put("lastMovePlayerId", lastMovePlayerId);
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
