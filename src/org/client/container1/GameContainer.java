package org.client.container1;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.json.client.*;

import org.client.container1.GameApi.Operation;
import org.client.container1.GameApi.EndGame;

public class GameContainer {  
	
  private final GameServer gameServer = new GameServer();
  private String accessSignature = "87b77c0116314d2884d591fd8436f82f";
  private String myPlayerId = "5910974510923776";
  private String gameId = "4905660105883648";
  private boolean set = true;
  private List<String> playerIds = Lists.newArrayList();
  
  private JSONSerialization js = new JSONSerialization();
	
  public GameContainer() {
  }
  
  public void insertNewMatch(String gameId, List<String> playerIds) {
    gameServer.sendInsertNewMatch(accessSignature, gameId, playerIds, this, myPlayerId);
  }
  
  // Overload the insertNewMatch function for UI component One
  public void insertNewMatch(String accessSignature, String myPlayerId, String gameId, List<String> playerIds) {
      this.accessSignature = accessSignature;
      this.myPlayerId = myPlayerId;
      gameServer.sendInsertNewMatch(accessSignature, gameId, playerIds, this, myPlayerId);
    }
  
  public void sendMakeMove(JSONArray move) {
   	gameServer.sendMakeMove(move);
  }
  
  private void setAttributes() {
	playerIds.add("4804625295212544");
	playerIds.add("5910974510923776");
	JSONArray playerIdsJSON = new JSONArray();
	for(int i=0;i<playerIds.size();i++) {
	  playerIdsJSON.set(i, new JSONString(playerIds.get(i)));
	}
    gameServer.setMyPlayerId(new JSONString(myPlayerId)); 
    gameServer.setGameContainer(this);
    gameServer.setAccessSignature(new JSONString(accessSignature));
    gameServer.setGameId(new JSONString(gameId));
    gameServer.setPlayerIds(playerIdsJSON);
  }
  
  public void endGame() {
	//test("end game1");
    List<Operation> endGameOperation = Lists.newArrayList();
    //test("end game2");
    EndGame eg = new EndGame(myPlayerId);
    //test("end game3");
    endGameOperation.add(eg);
    //test("end game4");
    JSONArray endGameOperationJSON = js.serializeMove(endGameOperation);
    //test("end game5");
    sendMakeMove(endGameOperationJSON);
  }
  
  public void updateGame() {
    //test("I'm container to get the new state");
    gameServer.getNewState();
  }
  
  public void gameStart() {
   if(set){
     //test("Set the Attributes firstly");
	  setAttributes();
	  set = false;
	}
    //test("I'm starting the game in the container");
    gameServer.getNewMatchInfo();
    test("Game Start");
  }
  
  public void updateUi(JSONObject state, JSONArray lastMove, JSONString lastMovePlayerId) {
	//test("SSSSSS");
    JSONObject message = new JSONObject();
    message.put("state", state);
    message.put("lastMove", lastMove);
    message.put("lastMovePlayerId", lastMovePlayerId);
    //test("LLLLL");
    sendMessage(message.toString());
  }
  
  public native void sendMessage(String message) /*-{
  	//alert("TTTTT");
    var iframe = $doc.getElementById("child");
    //alert("OOOOO");
    //alert(message);
    var jsonStr = JSON.parse(message);
    iframe.contentWindow.postMessage(jsonStr, "*");
    //alert("FFFFF");
  }-*/;
  
  //Just for test
  private native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}
