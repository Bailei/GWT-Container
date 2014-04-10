package org.client.container2;

import java.util.List;
import java.util.Map;

import org.client.container2.GameApi.Operation;
import org.client.container2.GameApi.VerifyMoveDone;
import org.client.container2.GameApi.Game;
import org.client.container2.GameApi.Container;
import org.client.container2.GameApi.UpdateUI;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.gwt.json.client.*;

/**
 * API:Interface for GameContainer 
 * @author Xiaocong Wang
 *
 */
public class GameControllerSyn implements Container{
  //private String myPlayerId = "4812957531766784";
  private String myPlayerId = "6022506221666304";
  
  private String lastMovePlayerId = "0";
  private final List<String> playerIds = Lists.newArrayList();
  private final List<Map<String, Object>> playersInfo = Lists.newArrayList();
  private Map<String, Object> lastState = Maps.newHashMap();
  private Map<String, Integer> playerIdToNumberOfTokensInPot = Maps.newHashMap();
  
  private Game game = null;
  private int playerNumber;
  private String gameId = "4794849949646848";
  
  private final JSONDeserialization jds = new JSONDeserialization();
  private final JSONSerialization js = new JSONSerialization();
  
  public GameControllerSyn(Game game, int playerNumber) {
	MessageListenerInControllerSyn.setGameController(this);
	this.game = game;
	this.playerNumber = playerNumber;
  }
  
  @Override
  public void sendVerifyMoveDone(VerifyMoveDone verifyMoveDone) {
  }
  
  @Override
  public void sendGameReady() {
  }
  
  public void sendEnterQueue() {
	setListener();
    JSONObject message = new JSONObject();
    message.put("request", new JSONString("SEND_ENTER_QUEUE"));
    message.put("gameId", new JSONString(gameId));
    sendMessage(message.toString());
  }
	
  public native void sendMessage(String message) /*-{
    $wnd.parent.postMessage(message, "*");
  }-*/;

  public native void setListener() /*-{
    $wnd.addEventListener(
    "message", 
    function(event) {
      @org.client.container2.MessageListenerInControllerSyn::messageListener(Ljava/lang/String;) (event.data);
    },	
    false);
  }-*/;
  
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
  
  public List<String> getPlayerIds() {
    return playerIds;
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
  
}

class MessageListenerInControllerSyn {

  private static GameControllerSyn gameControllerSyn = null;
  
  public static void setGameController(GameControllerSyn gameControllerSyn) {
    MessageListenerInControllerSyn.gameControllerSyn = gameControllerSyn;  
  }

  public static void messageListener(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
	  if(res != null) {
	    Map<String, Object> state = 
	    		gameControllerSyn.getJDS().deserializeState(res.get("state").isObject());
		List<Operation> lastMove = 
				gameControllerSyn.getJDS().deserializeMove(res.get("lastMove").isArray());
		String lastMovePlayerId = 
				res.get("lastMovePlayerId").isString().stringValue();
		JSONArray playerIds = res.get("playerIds").isArray();
		for(int i=0;i<playerIds.size();i++) {
		  gameControllerSyn.getPlayerIds().add(playerIds.get(i).isString().stringValue());
		}
	    for(int i=0; i<playerIds.size(); i++) {
	       gameControllerSyn.getPlayersInfo().add(ImmutableMap.<String, Object>of("playerId", gameControllerSyn.getPlayerIds().get(i)));
	    }
		UpdateUI updateUi = new UpdateUI(
		    gameControllerSyn.getMyPlayerId(), 
		    gameControllerSyn.getPlayersInfo(),
		    state, 
		    gameControllerSyn.getLastState(),
		    lastMove,
		    lastMovePlayerId,
		    gameControllerSyn.getPlayerIdToNumberOfTokensInPot());
		gameControllerSyn.updateUi(updateUi);
		gameControllerSyn.setLastState(state);
	  }else {
	    throw new RuntimeException("JSON Parse Error!");
	  }
  }
}
