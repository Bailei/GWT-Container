package org.client.container1;

import java.util.List;
import java.util.Map;

import org.client.container1.GameApi.Operation;
import org.client.container1.GameApi.VerifyMoveDone;
import org.client.container1.GameApi.Game;
import org.client.container1.GameApi.Container;
import org.client.container1.GameApi.UpdateUI;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.gwt.json.client.*;

import org.client.container1.JSONDeserialization;
import org.client.container1.JSONSerialization;

/**
 * API:Interface for GameContainer 
 * @author Xiaocong Wang
 *
 */
public class GameControllerAsyn implements Container{
  
  private String myPlayerId = "5910974510923776";
  private final List<String> playerIds = Lists.newArrayList();
  private final List<Map<String, Object>> playersInfo = Lists.newArrayList();
  private Map<String, Object> lastState = Maps.newHashMap();
  private String lastMovePlayerId = "0";
  private Map<String, Integer> playerIdToNumberOfTokensInPot = Maps.newHashMap();
  
  private Game game = null;
  private int playerNumber;
  private String gameId = "4905660105883648";
  
  private final JSONDeserialization jds = new JSONDeserialization();
  private final JSONSerialization js = new JSONSerialization();
  
  public GameControllerAsyn(Game game, int playerNumber) {
	/*The first not
	 *The second yes
	 */
	setListener();
	  
	  
	MessageListenerInControllerAsyn.setGameController(this);
	this.game = game;
	this.playerNumber = playerNumber;
    playerIds.add("4804625295212544");
    playerIds.add("5910974510923776");
	for(int i=0; i<playerIds.size(); i++) {
	  playersInfo.add(ImmutableMap.<String, Object>of("playerId", playerIds.get(i)));
	}
  }
  
  @Override
  public void sendGameReady() {
	setListener();
	JSONObject message = new JSONObject();
	message.put("request", new JSONString("INSERT_NEW_MATCH"));
	message.put("gameId", new JSONString(gameId));
	JSONArray playerIdsJSON = new JSONArray();
	for(int i=0; i<playerIds.size(); i++) {
	  playerIdsJSON.set(i, new JSONString(playerIds.get(i)));
	}
	message.put("playerIds", playerIdsJSON);
    sendMessage(message.toString());
  }
  
  public native void sendMessage(String message) /*-{
    $wnd.parent.postMessage(message, "*");
  }-*/;
  
  public native void setListener() /*-{
    $wnd.addEventListener("message", function(event) {
      var response = event.data;
      var str = JSON.stringify(response);
      @org.client.container1.MessageListenerInControllerAsyn::messageListener(Ljava/lang/String;) (str);
    },	
    false);
  }-*/;
	
  @Override
  public void sendVerifyMoveDone(VerifyMoveDone verifyMoveDone) {
  }
    
  @Override
  public void sendMakeMove(List<Operation> operations) {
	JSONObject message = new JSONObject();
	message.put("request", new JSONString("SEND_MAKE_MOVE"));
    message.put("move", js.serializeMove(operations));
    sendMessage(message.toString());
  }
	
  public void updateUi(UpdateUI updateUi) {
    game.sendUpdateUI(updateUi);
  }
  
  public JSONDeserialization getJDS() {
    return jds;
  }
  
  public JSONSerialization getJS() {
    return js;
  }
  
  public String getMyPlayerId() {
    return myPlayerId;
  }
  
  public List<Map<String, Object>> getPlayersInfo() {
    return playersInfo;
  }
  
  public Map<String, Object> getLastState() {
    return lastState;
  }
  
  public Map<String, Integer> getPlayerIdToNumberOfTokensInPot() {
    return playerIdToNumberOfTokensInPot;
  }
  
  public void setLastState(Map<String, Object> state) {
    this.lastState = state;
  }
  
  //The following code are just for test
  private native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
  
}

class MessageListenerInControllerAsyn {

  private static GameControllerAsyn gameControllerAsyn = null;
  
  public static void setGameController(GameControllerAsyn gameControllerAsyn) {
    MessageListenerInControllerAsyn.gameControllerAsyn = gameControllerAsyn;  
  }

  public static void messageListener(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
	if(res != null) {
	  Map<String, Object> state = 
			  gameControllerAsyn.getJDS().deserializeState(res.get("state").isObject());
	  List<Operation> lastMove = 
			  gameControllerAsyn.getJDS().deserializeMove(res.get("lastMove").isArray());
	  String lastMovePlayerId = 
			  res.get("lastMovePlayerId").isString().stringValue();	
	  UpdateUI updateUi = new UpdateUI(
		    gameControllerAsyn.getMyPlayerId(), 
		    gameControllerAsyn.getPlayersInfo(),
		    state, 
		    gameControllerAsyn.getLastState(),
		    lastMove,
		    lastMovePlayerId,
		    gameControllerAsyn.getPlayerIdToNumberOfTokensInPot());
		gameControllerAsyn.updateUi(updateUi);
		gameControllerAsyn.setLastState(state);
	  }else {
	    throw new RuntimeException("JSON Parse Error!");
	  }
  }
  
  //The following code are just for test
  private static native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
  
}
