package org.client.container;

import java.util.Map;
import java.util.List;

import org.client.container.GameApi.Game;
import org.client.container.GameApi.UpdateUI;
import org.client.container.GameApi.Operation;
import org.client.container.GameApi.VerifyMoveDone;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;

import com.google.gwt.json.client.*;

public class Container{
  private final String PLAYER_ID = "playerId";
  
  private final Map<String, Integer> convert = Maps.newHashMap();
  private final String myPlayerIdString;
  private String lastMovePlayerIdString;
  private final List<String> playerIdsString = Lists.newArrayList();

  private final Server server;
  private int myPlayerId;
  private final List<Integer> playerIds = Lists.newArrayList();
  private final List<Map<String, Object>> playersInfo = Lists.newArrayList();
  private Map<String, Object> lastState = Maps.newHashMap();
  private int lastMovePlayerId = 0;
  private Map<Integer, Integer> playerIdToNumberOfTokensInPot = Maps.newHashMap();
  private String accessSignature;
  private int gameId;
  
	private JSONSerialization js = null;
	private JSONDeserialization jds = null;
	
  public Container() {
	 
	myPlayerIdString = "5754903989321728";
	lastMovePlayerIdString = "0";
	playerIdsString.add("5754903989321728");
	playerIdsString.add("6197337160417280");
	convert.put("5754903989321728", 41);
	convert.put("6197337160417280", 42);
	  
	  
    server = new Server();
    JSONSerialization js = new JSONSerialization();
    JSONDeserialization jds = new JSONDeserialization();
    playerIds.add(41);
    playerIds.add(42);
    gameId = 123;
    accessSignature = "7392490120f76a6f4f7a32cbbe152b50";
    myPlayerId = 41;
    for(int i=0; i<playerIds.size(); i++) {
      playersInfo.add(ImmutableMap.<String, Object>of(PLAYER_ID, playerIds.get(i)));
    }
  }
  
  public void sendGameReady() {
    server.sendInsertNewMatch(accessSignature, 
        gameId, playerIdsString, myPlayerIdString, this);
  }
  
  public void sendMakeMove(JSONArray move) {
	server.sendMakeMove(move);
  }
  
  public void sendVerifyMoveDone(VerifyMoveDone verifyMove) {
  }
  
  public List<Integer> getPlayerIds() {
    return playerIds;
  }
  
  public void updateGame() {
    server.getMatchInfo();
  }
  
  public void updateUi(Map<String, Object> state, List<Operation> lastMove, String turn) {
	 JSONObject message = new JSONObject();
	 message.put("state", js.serializeState(state));
	 message.put("lastMove", js.serializeMove(lastMove));
	 message.put("turn", new JSONNumber(convert.get(turn)));
	 SendStateToGameControllerMessageListener listener = new SendStateToGameControllerMessageListener();
	 listener.sendMessage(message);
  }
  
  public class SendStateToGameControllerMessageListener {
    private void receivedMessage(String jsonStringMessage) {
	JSONArray move = JSONParser.parseStrict(jsonStringMessage).isArray();
	sendMakeMove(move);
    }
    
    public native void sendMessage(JSONObject message) /*-{
      $wnd.parent.frame[0].postMessage(message, '*');
      $wnd.addEventListener(
	      'message',  
	      function(e) {
	       var val = e.data;
	       this.@org.client.container.Container.SendStateToGameControllerMessageListener::receivedMessage(Ljava/lang/String;) (val);
	      },
	      false);
    }-*/;
  }
}
